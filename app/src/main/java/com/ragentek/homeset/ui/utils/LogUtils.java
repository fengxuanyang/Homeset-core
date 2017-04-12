package com.ragentek.homeset.ui.utils;

import android.util.Log;

/**
 * Created by wei.zhao1 on 2017/3/22.
 */

public class LogUtils {
    private static final boolean ENABLE_DEBUG = true;
    private static final String TAG = "UI";

    public static void d(String tag, String msg) {
        if (ENABLE_DEBUG) {
            Log.d(TAG, tag + ":" + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (ENABLE_DEBUG) {
            Log.i(TAG, tag + ":" + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (ENABLE_DEBUG) {
            Log.w(TAG, tag + ":" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (ENABLE_DEBUG) {
            Log.v(TAG, tag + ":" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ENABLE_DEBUG) {
            Log.e(TAG, tag + ":" + msg);
        }
    }
}