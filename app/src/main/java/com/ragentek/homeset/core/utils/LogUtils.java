package com.ragentek.homeset.core.utils;

import android.util.Log;
import com.ragentek.homeset.core.HomesetApp;

public class LogUtils {
    private static final String MAIN_TAG = "HomesetCore";
    private static final String ERROR_TAG = "Error";
    private static final String EVENT_TAG = "Event";

    public static void d(String tag, String message) {
        if (HomesetApp.isEnableDebugLog()) {
            Log.d(tag, message);
        }
    }

    public static void e(String subTag, String message) {
        Log.d(MAIN_TAG, ERROR_TAG + "/" + subTag +": "+ message);
    }

    public static void e(String subTag, String message, Throwable throwable) {
        Log.d(MAIN_TAG, ERROR_TAG + "/" + subTag +": "+ message + ", " + throwable.toString());
        StackTraceElement[] stackElements = throwable.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                Log.d(MAIN_TAG, ERROR_TAG + "/" + subTag +": "+ "          " + stackElements[i]);
            }
        }
    }

    public static void event(String subTag, String message) {
        if (HomesetApp.isEnableEventLog()) {
            Log.d(MAIN_TAG, EVENT_TAG + "/" + subTag + ": " + message);
        }
    }
}
