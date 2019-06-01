package com.jeff_jeong.gesturefeature;


// Created by Jeff_Jeong on 2019. 5. 31.


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeff_jeong.gesturefeature.adapters.ProductPagerAdapter;
import com.jeff_jeong.gesturefeature.customviews.MyDragShadowBuilder;
import com.jeff_jeong.gesturefeature.databinding.ActivityViewProductBinding;
import com.jeff_jeong.gesturefeature.models.Product;
import com.jeff_jeong.gesturefeature.resources.Products;
import com.jeff_jeong.gesturefeature.util.CartManager;

import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        View.OnDragListener
{

    private static final String TAG = "제품상세페이지액티비티";


    // data binding
    ActivityViewProductBinding mBinding;

    //widgets
    private ViewPager mProductContainerViewPager;
    private TabLayout mTabLayout;
    private RelativeLayout mAddToCart, mCart;
    private ImageView mCartIcon, mPlusIcon;



    //vars
    private Product mProduct;
    private ProductPagerAdapter mPagerAdapter;
    private GestureDetector mGestureDetector;
    private Rect mCartPositionRectangle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 바인딩 객체 인스턴스화
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_product);

        mProductContainerViewPager = findViewById(R.id.product_container);
        mTabLayout = findViewById(R.id.tab_layout);
        mAddToCart = findViewById(R.id.add_to_cart);
        mCart = findViewById(R.id.cart);
        mCartIcon = findViewById(R.id.cart_image);
        mPlusIcon = findViewById(R.id.plus_image);




        mAddToCart.setOnClickListener(this);
        mCart.setOnClickListener(this);

        // 제품 뷰페이저에 터치 리스너를 설정한다.
        mProductContainerViewPager.setOnTouchListener(this);

        // 제스쳐 디텍터
        mGestureDetector = new GestureDetector(this, this);

        // 제품 인텐트를 받으면
        // 제품 정보를 받는다.
        getIncomingIntent();
        // 뷰페이저 를 시작한다.
        initViewPager();
    }

    // 들어오는 인텐트를 가져오는 메소드
    private void getIncomingIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_product))){
            mProduct = intent.getParcelableExtra(getString(R.string.intent_product));
        }
    }


    // 뷰페이저를 시작하는 메소드
    private void initViewPager(){
        // 프래그먼트 리스트
        ArrayList<Fragment> fragments = new ArrayList<>();

        // 제품들 데이터
        Products products = new Products();

        // 선택된 제품들
        Product[] selectedProducts = products.PRODUCT_MAP.get(mProduct.getType());

        for(Product product: selectedProducts){
            // 프래그먼트 리스트에 각각의 제품을 번들에 넣어 매개변수로 설정한다.
            Bundle productBundle = new Bundle();
            productBundle.putParcelable(getString(R.string.intent_product), product);

            ViewProductFragment viewProductFragment = new ViewProductFragment();
            // 프래그먼트에 매개변수 설정
            viewProductFragment.setArguments(productBundle);
            fragments.add(viewProductFragment);
        }

        // 페이저 어답터 인스턴스화
        mPagerAdapter = new ProductPagerAdapter(getSupportFragmentManager(), fragments);
        // 뷰페이저에 어답터를 설정한다.
        mProductContainerViewPager.setAdapter(mPagerAdapter);
        // 텝 레이아웃을 뷰페이저를 넣어 설정한다.
        mTabLayout.setupWithViewPager(mProductContainerViewPager, true);
    }

    // 드래그 모드를 설정하는 메소드
    private void setDragMode(boolean isDragging){
        if(isDragging){
            mCartIcon.setVisibility(View.INVISIBLE);
            mPlusIcon.setVisibility(View.VISIBLE);
        }
        else{
            mCartIcon.setVisibility(View.VISIBLE);
            mPlusIcon.setVisibility(View.INVISIBLE);
        }
    }

    // 현재 아이템을 장바구니에 넣는 메소드
    private void addCurrentItemToCart(){
        // 선택된 제품을 가져온다.
        // 선택된 프래그먼트의 제품
        Product selectedProduct = ((ViewProductFragment)mPagerAdapter.getItem(mProductContainerViewPager.getCurrentItem())).mProduct;

        // 장바구니 매니저
        CartManager cartManager = new CartManager(this);
        // 장바구니에 선택된 제품을 추가한다.
        cartManager.addItemToCart(selectedProduct);
        Toast.makeText(this, "상품이 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show();

    }

    // 장바구니의 뷰상의 위치를 가져오는 메소드
    private void getCartPosition(){
        mCartPositionRectangle = new Rect();
        mCart.getGlobalVisibleRect(mCartPositionRectangle);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        mCartPositionRectangle.left = mCartPositionRectangle.left - Math.round((int)(width * 0.18));
        mCartPositionRectangle.top = 0;
        mCartPositionRectangle.right = width;
        mCartPositionRectangle.bottom = mCartPositionRectangle.bottom - Math.round((int)(width * 0.03));
    }

    // 전체화면 프래그먼트를 보여주는 메소드
    private void inflateFullScreenProductFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FullScreenProductFragment fragment = new FullScreenProductFragment();

        Bundle bundle = new Bundle();
        Product selectedProduct =((ViewProductFragment)mPagerAdapter.getItem(mProductContainerViewPager.getCurrentItem())).mProduct;
        bundle.putParcelable(getString(R.string.intent_product), selectedProduct);
        fragment.setArguments(bundle);

        // Enter Transition for New Fragment
        Fade enterFade = new Fade();
        enterFade.setStartDelay(1);
        enterFade.setDuration(300);
        fragment.setEnterTransition(enterFade);

        transaction.addToBackStack(getString(R.string.fragment_full_screen_product));
        transaction.replace(R.id.full_screen_container, fragment, getString(R.string.fragment_full_screen_product));
        transaction.commit();
    }


    // 터치가 이루어 질때
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // 장바구니의 경계선 위치를 가져온다.
        getCartPosition();


        // 제품 뷰페이저를 터치하면
        if(v.getId() == R.id.product_container){
            // 제스쳐 디텍터 설정
            mGestureDetector.onTouchEvent(event);
        }

//        int action = event.getAction();
//
//        // 들어오는 액션에 따라 분기 처리
//        switch(action){
//            case(MotionEvent.ACTION_DOWN):
//                Log.d(TAG, "onTouch: ACTION_DOWN");
//                return false;
//                // 반환이 true 이면 이 이벤트를 제외한 다른 액션은 받지 않는다.
//
//            case(MotionEvent.ACTION_MOVE):
//                Log.d(TAG, "onTouch: ACTION_MOVE");
//                return false;
//
//            case(MotionEvent.ACTION_UP):
//                Log.d(TAG, "onTouch: ACTION_UP");
//                return false;
//
//            case(MotionEvent.ACTION_CANCEL):
//                Log.d(TAG, "onTouch: ACTION_CANCEL");
//                return false;
//
//            case(MotionEvent.ACTION_OUTSIDE):
//                Log.d(TAG, "onTouch: ACTION_OUTSIDE : 현재 화면요소 밖에서 움직임이 일어났을때 ");
//                return false;
//        }


        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown: ");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: ");
    }

    @Override   
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp: ");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll: ");
        return false;
    }

    // 길게 눌렀을때
    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: ");

        // 선택된 프래그먼트를 가져온다.
        ViewProductFragment fragment = ((ViewProductFragment)mPagerAdapter.getItem(mProductContainerViewPager.getCurrentItem()));
        // 드래그 이미지를 만든다.
        View.DragShadowBuilder myShadow = new MyDragShadowBuilder(
                ((ViewProductFragment)fragment).mImageView,
                fragment.mProduct.getImage()
        );

        // 드래그 이미지를 그린다.
        ((ViewProductFragment)fragment).mImageView.startDrag(null,
                myShadow,
                null,
                0);

        // 드래그 이미지에 드래그 리스너를 설정한다.
        myShadow.getView().setOnDragListener(this);


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling: ");
        return false;
    }

    // 더블탭 리스너 액션

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed: ");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: ");

        // 전체 화면 제품 프래그먼트를 보여준다.
        inflateFullScreenProductFragment();

        return false;

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent: ");

        return false;
    }

    //
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cart:{
                // 장바구니 액티비티를 연다.
                break;
            }

            case R.id.add_to_cart:{
                // 현재 아이템을 장바구니에 추가한다.
                addCurrentItemToCart();
                break;
            }

        }
    }

    // OnDragListener
    // 드래그가 이루어질떄
    @Override
    public boolean onDrag(View v, DragEvent event) {

        switch(event.getAction()) {

            // 드래그가 시작됬을때
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d(TAG, "onDrag: drag started.");
                // 드래그를 시작한다.
                setDragMode(true);
                return true;

                // 영역안에 들어왔을때
            case DragEvent.ACTION_DRAG_ENTERED:

                return true;

                // 영역 안에서 움직일때
            // 여기서 드래그의 위치를 추적한다.
            case DragEvent.ACTION_DRAG_LOCATION:
                Point currentPoint = new Point(Math.round(event.getX()), Math.round(event.getY()));

                // 드래그 의 경계가 장바구니 영역에 들어가면
                if(mCartPositionRectangle.contains(currentPoint.x, currentPoint.y)){
                    // 장바구니 아이콘의 배경색을 변경한다.
                    mCart.setBackgroundColor(this.getResources().getColor(R.color.blue2));
                }
                else {
                    mCart.setBackgroundColor(this.getResources().getColor(R.color.blue1));
                }

                return true;

                // 영역 안을 나갔을때
            case DragEvent.ACTION_DRAG_EXITED:

                return true;

                // 영역 안에서 손을 땠을때
            case DragEvent.ACTION_DROP:

                Log.d(TAG, "onDrag: dropped.");

                return true;

                // 위치와 상관없이 드래그가 끝났을때
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(TAG, "onDrag: ended.");
                Drawable background = mCart.getBackground();
                if (background instanceof ColorDrawable) {
                    if (((ColorDrawable) background).getColor() == getResources().getColor(R.color.blue2)) {
                        addCurrentItemToCart();
                    }
                }
                mCart.setBackground(this.getResources().getDrawable(R.drawable.blue_onclick_dark));
                setDragMode(false);
                return true;

            // An unknown action type was received.
            default:
                Log.e(TAG,"Unknown action type received by OnStartDragListener.");
                break;
        }

        return false;
    }

}