package com.ragentek.homeset.core.utils;

import android.util.Log;

import java.lang.reflect.Method;

public class SystemPropertiesInvoke {

    private static final String TAG = "SystemPropertiesInvoke";
    private static Method setMethod = null;

    public static void start(final String name) {
        try {
            if (setMethod == null) {
                setMethod = Class.forName("android.os.SystemService").getMethod("start", String.class);
                Log.e(TAG, "setMethod : " +setMethod.getName());
            }
            setMethod.invoke(null, name);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Platform error: " + e.toString());
            return ;
        }
    }
}
