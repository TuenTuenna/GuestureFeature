package com.jeff_jeong.gesturefeature.touchhelpers;


// Created by Jeff_Jeong on 2019. 6. 1.

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jeff_jeong.gesturefeature.CartRecyclerViewAdapter;

// 장바구니 아이템 커스텀 아이템 터치 헬퍼 클래스
public class CartItemTouchHelperCallback extends ItemTouchHelper.Callback {

    // 아이템 터치 헬퍼 어답터
    private final ItemTouchHelperAdapter mAdapter;

    // 생성자 메소드
    public CartItemTouchHelperCallback(ItemTouchHelperAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    // 아이템이 스와이프 가능
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    // 리사이클러뷰 아이템을 드래그할수 있게
    @Override
    public boolean isLongPressDragEnabled() {
        // false 로 기능 상실
        return false;
    }

    // 이동 플래그를 가져올때
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        // 뷰홀더 인스턴스에 따라 처리
        if(viewHolder instanceof CartRecyclerViewAdapter.SectionHeaderViewHolder){
            return 0;
        }
        // 드래그는 위, 아래
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        // 스와이프는 시작, 끝
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        // 이동 플래그를 만든다.
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // 이동할때
    @Override
    public boolean onMove( RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
        // 아이템 터치 핼퍼 인터페이스에 아이템이 스와이프 됬다고 알려준다.
        mAdapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());


        return true;
    }

    // 스와이프를 할때
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        // 아이템 터치 핼퍼 인터페이스에 아이템이 스와이프 됬다고 알려준다.
        mAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }


    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        // 선택된 아이템의 배경색 흰색으로 설정
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }


    // 선택된 아이템
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        // 드래그중이면
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            // 선택된 아이템의 배경색 회색으로 변경
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }

    }


}
