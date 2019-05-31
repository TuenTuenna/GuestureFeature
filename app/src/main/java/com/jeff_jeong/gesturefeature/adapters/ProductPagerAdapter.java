package com.jeff_jeong.gesturefeature.adapters;


// Created by Jeff_Jeong on 2019. 5. 31.

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

// 제품 페이저 어답터
// 프래그먼트 스테이트 페이저 어답터는 리스트를 페이저에 담기에 좋다.
public class ProductPagerAdapter extends FragmentStatePagerAdapter {

    // 리스트를 잡고 있을 글로벌 변수
    private ArrayList<Fragment> mFragments = new ArrayList<>();


    // 생성자 메소드
    public ProductPagerAdapter(FragmentManager fm, ArrayList<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
