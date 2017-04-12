package com.ragentek.homeset.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ragentek.homeset.weather.utils.LogUtils;


public class WeatherReceiver extends BroadcastReceiver {
    private static final String TAG = "WeatherReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            LogUtils.d(TAG, "onReceive, ACTION_BOOT_COMPLETED");

            Intent it = new Intent(context, WeatherService.class);
            context.startService(it);
        }
    }
}