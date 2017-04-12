package com.ragentek.homeset.speech.utils;

import android.util.Log;

public class LogUtils {
    private static final boolean DEBUG = true;

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        LogUtils.e(tag, message);
    }

}
