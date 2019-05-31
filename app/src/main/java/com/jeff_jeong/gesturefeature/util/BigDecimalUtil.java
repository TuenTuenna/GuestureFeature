package com.jeff_jeong.gesturefeature.util;


// Created by Jeff_Jeong on 2019. 5. 31.


import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalUtil {

    public static String getValue(BigDecimal value){
        DecimalFormat df = new DecimalFormat("###,###,###.00");
        return String.valueOf(df.format(value));
    }


}