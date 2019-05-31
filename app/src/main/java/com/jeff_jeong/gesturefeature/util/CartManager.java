package com.jeff_jeong.gesturefeature.util;


// Created by Jeff_Jeong on 2019. 5. 31.


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jeff_jeong.gesturefeature.models.Product;
import com.jeff_jeong.gesturefeature.resources.Products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// 장바구니 관리 클래스
public class CartManager {

    private static final String TAG = "CartManger";

    static final String SHOPPING_CART = "shopping_cart";
    static final String CART_ITEMS = "cart_items";
    Context mContext;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public CartManager(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(SHOPPING_CART, 0);
        mEditor = mSharedPreferences.edit();
    }

    public void addItemToCart(Product product){
        Set<String> cartItems = mSharedPreferences.getStringSet(CART_ITEMS, new HashSet<String>());
        cartItems.add(String.valueOf(product.getSerial_number()));

        mEditor.putStringSet(CART_ITEMS, cartItems);
        mEditor.commit();
    }

    public ArrayList<Product> getCartItems(){
        Set<String> cartItems = mSharedPreferences.getStringSet(CART_ITEMS, new HashSet<String>());

        ArrayList<Product> productsList = new ArrayList<>();
        HashMap<String, Product> productMap = Products.getProducts();
        for(String serialNumber : cartItems){
            productsList.add(productMap.get(serialNumber));
            Log.d(TAG, "getCartItem: serial number: " + serialNumber);
            Log.d(TAG, "getCartItem: item title: " + productMap.get(serialNumber).getTitle());
        }

        return productsList;
    }

    public void removeItemFromCart(Product product){
        Set<String> cartItems = mSharedPreferences.getStringSet(CART_ITEMS, new HashSet<String>());

        cartItems.remove(String.valueOf(product.getSerial_number()));
        mEditor.putStringSet(CART_ITEMS, cartItems);
        mEditor.commit();
    }


}
