package com.jeff_jeong.gesturefeature.touchhelpers;


// Created by Jeff_Jeong on 2019. 6. 1.

// 아이템 터치 헬퍼 어답터 인터페이스
public interface ItemTouchHelperAdapter {

    // 아이템이 이동되었을때
    void onItemMoved(int fromPosition, int toPosition);

    // 아이템이 스와이프 되었을때
    void onItemSwiped(int position);
}
