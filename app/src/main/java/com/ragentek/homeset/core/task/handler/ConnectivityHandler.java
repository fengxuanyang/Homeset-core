package com.ragentek.homeset.core.task.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.base.push.PushEngine;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.TaskManager;

public class ConnectivityHandler {
    private static final String TAG = ConnectivityHandler.class.getSimpleName();

    private BaseContext mBaseContext;
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private PushEngine mPushEngine;

    private boolean mIsRunning = false;
    NetworkStateReceiver mReceiver = new NetworkStateReceiver();

    public ConnectivityHandler(TaskManager taskManager) {
        mBaseContext = taskManager.getBaseContext();
        mContext = mBaseContext.getAndroidContext();
        mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        mPushEngine = (PushEngine) mBaseContext.getEngine(EngineManager.ENGINE_PUSH);

    }

    public void startListening() {
        if (isRunning()) {
            return;
        }

        registerNetworkReceiver();
        setRunning(true);
    }

    public void stopListening() {
        if(!isRunning()) {
            return;
        }

        mPushEngine.disconnect();
        unregisterReceiver();
        setRunning(false);
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    private void setRunning(boolean isRunning) {
        this.mIsRunning = isRunning;
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    class NetworkStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkEnable()) {
                mPushEngine.connect();
            } else {
                mPushEngine.disconnect();
            }
        }
    }

    private boolean isWifiNetworkEnable() {
        NetworkInfo network = mConnectivityManager.getActiveNetworkInfo();
        if ((network != null) && (network.getType() == ConnectivityManager.TYPE_WIFI) && network.isConnected()) {
            return true;
        }
        return false;
    }

    private boolean isNetworkEnable() {
        NetworkInfo network = mConnectivityManager.getActiveNetworkInfo();
        if ((network != null) && network.isConnected()) {
            return true;
        }
        return false;
    }

}
