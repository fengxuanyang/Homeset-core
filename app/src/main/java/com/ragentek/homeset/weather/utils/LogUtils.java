package com.ragentek.homeset.weather.utils;

import android.util.Log;


public class LogUtils {
    public static void d(String tag, String msg) {
        if(Contants.ENABLE_DEBUG) {
            Log.d(Contants.TAG, tag + ":" + msg);
        }
    }

    public static void i(String tag, String msg) {
        if(Contants.ENABLE_DEBUG) {
            Log.i(Contants.TAG, tag + ":" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if(Contants.ENABLE_DEBUG) {
            Log.w(Contants.TAG, tag + ":" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if(Contants.ENABLE_DEBUG) {
            Log.v(Contants.TAG, tag + ":" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if(Contants.ENABLE_DEBUG) {
            Log.e(Contants.TAG, tag + ":" + msg);
        }
    }
}