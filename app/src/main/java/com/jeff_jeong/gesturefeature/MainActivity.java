package com.jeff_jeong.gesturefeature;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jeff_jeong.gesturefeature.models.Product;
import com.jeff_jeong.gesturefeature.resources.Products;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener
{

    private static final String TAG = "MainActivity";
    private static final int NUM_COLUMNS = 2;

    //vars
    MainRecyclerViewAdapter mAdapter;
    private ArrayList<Product> mProducts = new ArrayList<>();

    //widgets
    private RecyclerView mRecyclerView;
    private RelativeLayout mCart;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mCart = findViewById(R.id.cart);
        mCart.setOnClickListener(this);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // 스와이프리프레시 레이아웃에 리스너 설정하기
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // 제품들 가져오기
        getProducts();
        // 리사이클러뷰 시작하기
        initRecyclerView();
    }

    private void getProducts(){
        mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
    }

    private void initRecyclerView(){
        mAdapter = new MainRecyclerViewAdapter(this, mProducts);
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 장바구니 액티비티를 연다.
            case R.id.cart:{
                Intent intent = new Intent(this, ViewCartActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    // 레이아웃이 리프레쉬 될떄
    @Override
    public void onRefresh() {
        // fake 인데 데이터만 셔플하자 - 서버에서 데이터를 받아온다.
        Collections.shuffle(mProducts);
        // 아이템이 로딩되었다.
        onItemsLoadComplete();
    }

    // 아이템 로딩이 완료되었을때 실행되는 메소드
    private void onItemsLoadComplete(){
        // 리사이클러뷰 어답터에 데이터가 변경되었다고 알려준다.
        (mRecyclerView.getAdapter()).notifyDataSetChanged();
        // 리프레시 레이아웃의 애니메이션을 멈춘다.
        mSwipeRefreshLayout.setRefreshing(false);
    }


}
