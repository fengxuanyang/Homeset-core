package com.ragentek.homeset.ui.login;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.ui.utils.LogUtils;

public class LoginReceiver extends BroadcastReceiver {
    private static final String TAG = "LoginReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            LogUtils.d(TAG, "onReceive, ACTION_BOOT_COMPLETED");

            DeviceUtils deviceUtils = DeviceUtils.getInstance(context);
            deviceUtils.setLoginFlag("0");
        }
    }
}