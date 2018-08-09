package com.example.android.inventoryapp;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

    public static String formatPrice(double d){
        NumberFormat applyCurrency = NumberFormat.getCurrencyInstance(Locale.UK);
        applyCurrency.setMinimumFractionDigits(0);
        return applyCurrency.format(d);
    }

    public static String formatInputPrice(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
}
