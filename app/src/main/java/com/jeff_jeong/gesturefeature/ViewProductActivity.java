package com.jeff_jeong.gesturefeature;


// Created by Jeff_Jeong on 2019. 5. 31.


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jeff_jeong.gesturefeature.models.Product;

public class ViewProductActivity extends AppCompatActivity {

    private static final String TAG = "ViewProductActivity";

    //widgets

    //vars
    private Product mProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_product))){
            mProduct = intent.getParcelableExtra(getString(R.string.intent_product));
        }
    }



}