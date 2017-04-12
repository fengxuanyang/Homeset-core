package com.ragentek.homeset.audiocenter.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Created by xuanyang.feng on 2017/2/17.
 */

public class LogUtil {
    private final static String TAG = "AudioCenter";
    private final static String MATCH = "%s->%s->%d";
    private final static String CONNECTOR = ":<--->:";

    private static boolean SWITCH = true;

    private static String buildHeader() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        return stack == null ? "UNKNOWN" : String.format(Locale.getDefault(),
                MATCH, stack.getClassName(), stack.getMethodName(), stack.getLineNumber()) + CONNECTOR;
    }

    public static void v(String tag, Object msg) {
        if (SWITCH) Log.v(TAG, buildHeader() + msg.toString());
    }

    public static void d(String tag, Object msg) {
        if (SWITCH) Log.d(TAG, buildHeader() + msg.toString());
    }

    public static void i(String tag, Object msg) {
        if (SWITCH) Log.i(TAG, buildHeader() + msg.toString());
    }

    public static void w(String tag, Object msg) {
        if (SWITCH) Log.w(TAG, buildHeader() + msg.toString());
    }

    public static void e(String tag, Object msg) {
        if (SWITCH) Log.e(TAG, buildHeader() + msg.toString());
    }
}
