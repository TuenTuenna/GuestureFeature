package com.jeff_jeong.gesturefeature.customviews;


// Created by Jeff_Jeong on 2019. 5. 31.


import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

// 드래그 이미지 클래스
public class MyDragShadowBuilder extends View.DragShadowBuilder {

    private static Drawable shadow;


    public MyDragShadowBuilder(View view, int imageRessource) {
        super(view);

        // url 을 매개변수로 받을때는
        // Drawable 을 받아서 처리


        shadow = getView().getContext().getResources().getDrawable(imageRessource);


    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        int height;
        int width;
        int imageRatio;

        // 이미지 비율
        imageRatio = shadow.getIntrinsicWidth() / shadow.getIntrinsicHeight();

        width = getView().getWidth() / 2;
        height = width * imageRatio;


        // 경계를 설정
        shadow.setBounds(0,0,width,height);

        outShadowSize.set(width,height);

        outShadowTouchPoint.set(width / 2, height / 2);

    }

    // 그림자가 그려질때
    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }



}
