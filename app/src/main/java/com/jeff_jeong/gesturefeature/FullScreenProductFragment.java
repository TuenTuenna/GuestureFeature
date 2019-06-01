package com.jeff_jeong.gesturefeature;


// Created by Jeff_Jeong on 2019. 6. 1.

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jeff_jeong.gesturefeature.customviews.ScalingImageView;
import com.jeff_jeong.gesturefeature.models.Product;

public class FullScreenProductFragment extends Fragment

{

    private static final String TAG = "FullScreenProductFragme";

    //widgets
    private ScalingImageView mImageView;

    //vars
    public Product mProduct;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if(bundle != null){
            mProduct = bundle.getParcelable(getString(R.string.intent_product));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_product, container, false);
        //
        mImageView = view.findViewById(R.id.image);

        setProduct();

        return view;
    }

    // 제품을 설정하는 메소드
    private void setProduct(){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(mProduct.getImage())
                .into(mImageView);

    }


}
