package com.jeff_jeong.gesturefeature;


// Created by Jeff_Jeong on 2019. 6. 1.


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;

import com.jeff_jeong.gesturefeature.models.Product;
import com.jeff_jeong.gesturefeature.resources.ProductHeaders;
import com.jeff_jeong.gesturefeature.touchhelpers.CartItemTouchHelperCallback;
import com.jeff_jeong.gesturefeature.util.CartManager;

import java.math.BigDecimal;
import java.util.ArrayList;

// 장바구니 액티비티
public class ViewCartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewCartActivity";

    //widgets
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    //vars
    CartRecyclerViewAdapter mAdapter;
    private ArrayList<Product> mProducts = new ArrayList<>();
    private boolean mIsScrolling;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        mRecyclerView = findViewById(R.id.recycler_view);
        mFab = findViewById(R.id.fab);

        // 플로팅 액션 버튼에 클릭 리스너를 설정한다.
        mFab.setOnClickListener(this);

        // 제품들을 가져온다.
        getProducts();
        // 리사이클러뷰를 그린다.
        initRecyclerView();
    }

    // 제품들을 가져오는 메소드
    private void getProducts(){
        // 해더를 추가한다.
        mProducts.add(new Product(ProductHeaders.HEADER_TITLES[0], 0, "", new BigDecimal(0), 0));
        mProducts.add(new Product(ProductHeaders.HEADER_TITLES[1], 0, "", new BigDecimal(0), 0));
        mProducts.add(new Product(ProductHeaders.HEADER_TITLES[2], 0, "", new BigDecimal(0), 0));

        CartManager cartManager = new CartManager(this);
        //
        mProducts.addAll(cartManager.getCartItems());
    }

    // 리사이클러뷰를 시작하는 메소드
    private void initRecyclerView(){
        // 장바구니 리사이클러뷰 어답터
        mAdapter = new CartRecyclerViewAdapter(this, mProducts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // 장바구니 아이템 터치 핼퍼 콜백 객체 인스턴스화
        CartItemTouchHelperCallback callback = new CartItemTouchHelperCallback(mAdapter);
        // 아이템 터치 핼퍼 인스턴스화
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

        // 장바구니 리사이클러뷰 어답터에 아이템 터치 핼퍼를 설정한다.
        mAdapter.setTouchHelper(itemTouchHelper);

        // 아이템 터치 헬퍼에 리사이클러뷰를 붙인다.
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        // 리사이클러뷰 글로벌 레이아웃 리스너를 설정한다.
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 마시멜로우 버번에서는 스크롤 리스너 사용
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    // 리사이클러뷰에 커스텀 스크롤 리스너를 설정한다.
                    mRecyclerView.setOnScrollListener(new CartScrollListener());
                }
                else {
                    mRecyclerView.addOnScrollListener(new CartScrollListener());
                }
            }
        });




    }


    // 플로팅 액션버튼을 설정하는 메소드
    @SuppressLint("RestrictedApi")
    private void setFABVisibility(boolean isVisible){
        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        if(isVisible){
            mFab.setAnimation(animFadeIn);
            mFab.setVisibility(View.VISIBLE);
        }
        else{
            mFab.setAnimation(animFadeOut);
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    // 리사이클러뷰를 스크롤 할수 있는지 확인하는 메소드
    public boolean isRecyclerScrollable(){
        return mRecyclerView.computeVerticalScrollRange() > mRecyclerView.getHeight();
    }

    // 스크롤링을 설정하는 메소드
    public void setIsScrolling(boolean isScrolling){
        mIsScrolling = isScrolling;
    }

    // 스크롤링 여부를 가져오는 메소드
    public boolean isScrolling(){
        return mIsScrolling;
    }

    // 클릭이 되었을때
    @Override
    public void onClick(View v) {
        // 클릭 된 뷰가 플로팅 액션바 이면
        if(v.getId() == R.id.fab){
            // 리사이클러뷰 위치를 맨위로 설정한다.
            mRecyclerView.smoothScrollToPosition(0);
        }
    }


    // 커스텀 언스크롤 클래스
    class CartScrollListener extends RecyclerView.OnScrollListener{

        // 스크롤 상태가 변경되었을때
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                Log.d(TAG, "onScrollStateChanged: stopped");
            }

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                Log.d(TAG, "onScrollStateChanged: fling");
            }

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                Log.d(TAG, "onScrollStateChanged: touched");
            }
            // 스크롤링 설정
            setIsScrolling(true);

        }

        // 리사이클러뷰가 스크롤 되었을때
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            // 리사이클러뷰가 스크롤 가능할때
            if(isRecyclerScrollable()){
                if(!recyclerView.canScrollVertically(1)){
                    // 플로팅 액셥바를 보인다.
                    setFABVisibility(true);
                }
                else {
                    setFABVisibility(false);
                }
            }
            // 스크롤링 설정
            setIsScrolling(true);
        }


    }


}
