package com.jeff_jeong.gesturefeature.customviews;


// Created by Jeff_Jeong on 2019. 6. 1.

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

// 이미지를 스케일조정하는 클래스
public class ScalingImageView extends AppCompatImageView implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{



    private static final String TAG = "ScalingImageView";

    Context mContext;
    ScaleGestureDetector mScaleDetector;
    GestureDetector mGestureDetector;

    // Image States
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Scales
    float mSaveScale = 1f;
    float mMinScale = 1f;
    float mMaxScale = 4f;

    // view dimensions
    float origWidth, origHeight;
    int viewWidth, viewHeight;

    // 스케일일 트렌지션을 위한 매트릭스
    Matrix mMatrix;
    float[] mMatrixValues;

    // 위치를 추적하기 위해 사용
    PointF mLast = new PointF();
    PointF mStart = new PointF();


    public ScalingImageView(Context context) {
        super(context);
        // 스케일을 공유한다.
        sharedConstructing(context);
    }

    // 레이아웃파일에서 사용하기 위해 attribueSet 매개변수 필
    public ScalingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 스케일을 공유한다.
        sharedConstructing(context);
    }

    // 스케일을 공유하는 메소드
    private void sharedConstructing(Context context){
        super.setClickable(true);
        // 스케일 디텍터
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mMatrix = new Matrix();
        mMatrixValues = new float[9];
        setImageMatrix(mMatrix);
        setScaleType(ScaleType.MATRIX);

//        mMatrix.postTranslate(10, 20);

        // 제스쳐 디텍터를 설정한다.
        mGestureDetector = new GestureDetector(context,this);
        setOnTouchListener(this);

    }

    // 이미지를 스크린에 맞추는 메소드
    public void fitToScreen(){
        mSaveScale = 1;

        float scale;
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0)
            return;
        int bmWidth = drawable.getIntrinsicWidth();
        int bmHeight = drawable.getIntrinsicHeight();

        Log.d(TAG, "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

        float scaleX = (float) viewWidth / (float) bmWidth;
        float scaleY = (float) viewHeight / (float) bmHeight;
        scale = Math.min(scaleX, scaleY);
        mMatrix.setScale(scale, scale);

        // Center the image
        float redundantYSpace = (float) viewHeight
                - (scale * (float) bmHeight);
        float redundantXSpace = (float) viewWidth
                - (scale * (float) bmWidth);
        redundantYSpace /= (float) 2;
        redundantXSpace /= (float) 2;
        Log.d(TAG, "fitToScreen: redundantXSpace: " + redundantXSpace);
        Log.d(TAG, "fitToScreen: redundantYSpace: " + redundantYSpace);

        mMatrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = viewWidth - 2 * redundantXSpace;
        origHeight = viewHeight - 2 * redundantYSpace;
        setImageMatrix(mMatrix);
    }

    // 뷰의 공간을 계산할때
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 뷰의 가로 세로를 가져온다.
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 저장 스케일이 1이면
        if(mSaveScale == 1){
            // 이미지를 화면에 맞게 맞춘다.
            fitToScreen();
        }


    }





    // OnTouch 리스너
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        // 제스쳐 디텍터
        mScaleDetector.onTouchEvent(event);
        // 스케일 디텍터
        mGestureDetector.onTouchEvent(event);

        // 현재위치
        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLast.set(curr);
                mStart.set(mLast);
                mode = DRAG;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float dx = curr.x - mLast.x;
                    float dy = curr.y - mLast.y;

                    // 고정된 좌표
                    float fixTransX = getFixDragTranslation(dx, viewWidth, origWidth * mSaveScale);
                    float fixTransY = getFixDragTranslation(dy, viewHeight, origHeight * mSaveScale);

                    // 이미지 움직이기
                    mMatrix.postTranslate(fixTransX, fixTransY);
                    Log.d(TAG, "onTouch: fixTransX: " + fixTransX);
                    Log.d(TAG, "onTouch: fixTransY: " + fixTransY);
                    mLast.set(curr.x, curr.y);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        // 이미지에 변경사항 설정
        setImageMatrix(mMatrix);
        fixTranslation();

        return false;
    }

    // 위치를 고정하는 메소드
    void fixTranslation() {

        mMatrix.getValues(mMatrixValues); //put matrix values into a float array so we can analyze
        float transX = mMatrixValues[Matrix.MTRANS_X]; //get the most recent translation in x direction
        float transY = mMatrixValues[Matrix.MTRANS_Y]; //get the most recent translation in y direction

        float fixTransX = getFixTranslation(transX, viewWidth, origWidth * mSaveScale);
        float fixTransY = getFixTranslation(transY, viewHeight, origHeight * mSaveScale);

        if (fixTransX != 0 || fixTransY != 0)
            mMatrix.postTranslate(fixTransX, fixTransY);
    }

    // 고정된 위치를 가져오는 메소드
    // contentSize 는 실제 이미지 크기
    float getFixTranslation(float trans, float viewSize, float contentSize) {
        // minTrans 는 음수에서 0까지
        // maxTrans 는 0에서 뷰 사이즈 까지
        float minTrans, maxTrans;

        // 줌이 안되었을때
        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        }
        else { // 줌이 되었을떄
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans) { // negative x or y translation (down or to the right)
            Log.d(TAG, "getFixTranslation: minTrans: " + minTrans + ", trans: " + trans +
                    "\ndifference: " + (-trans + minTrans));
            return -trans + minTrans;
        }

        if (trans > maxTrans) { // positive x or y translation (up or to the left)
            Log.d(TAG, "getFixTranslation: maxTrans: " + maxTrans + ", trans: " + trans +
                    "\ndifference: " + (-trans + maxTrans));
            return -trans + maxTrans;
        }
        return 0;
    }

    // 드래그 영역 정해주는 메소드
    private float getFixDragTranslation(float delta, float viewSize, float contentSize){
        if(contentSize <= viewSize){
            return 0;
        }
        return delta;
    }


    // 언 제스쳐 리스너

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    // 언 더블탭 리스너
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // 화면에 이미지를 맞춘다. - 원래 대로 돌린다.
        fitToScreen();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    // 스케일 리스너
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        // 스케일이 시작되엇을때
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        // 스케일이 진행중일때
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "onScale: "+ detector.getScaleFactor());

            float mScaleFactor = detector.getScaleFactor();

            float origScale = mSaveScale;

            mSaveScale *= mScaleFactor;

            if (mSaveScale > mMaxScale) {
                mSaveScale = mMaxScale;
                mScaleFactor = mMaxScale / origScale;
            } else if (mSaveScale < mMinScale) {
                mSaveScale = mMinScale;
                mScaleFactor = mMinScale / origScale;
            }

            // 스케일 이미지 로직
            if (origWidth * mSaveScale <= viewWidth
                    || origHeight * mSaveScale <= viewHeight){
                mMatrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
                        viewHeight / 2);
                // 이미지를 확대할 때는 손가락이 있는 곳이 센터 포커스이고
                Log.d(TAG, "onScale: 센터 포커스 ");
            }

            else{
                // 이미지를 축소할 때는 센터 포커스가 변경된다.
                mMatrix.postScale(mScaleFactor, mScaleFactor,
                        detector.getFocusX(), detector.getFocusY());
                Log.d(TAG, "onScale: 포커싱 변경");
            }
            // 포커스를 중앙으로 맞춘다.
            fixTranslation();

            return true;
        }
    }


}
