package com.ragentek.homeset.audiocenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.ragentek.homeset.audiocenter.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by xuanyang.feng on 2017/3/1.
 */

public class AudioCenterBaseActivity extends AppCompatActivity {
    protected static String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getComponentName().getShortClassName();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
    }


}
