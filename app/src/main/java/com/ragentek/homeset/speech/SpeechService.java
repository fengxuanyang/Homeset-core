package com.ragentek.homeset.speech;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SpeechService extends Service {
    private SpeechManagerImpl mSpeechManager;

    @Override
    public void onCreate() {
        mSpeechManager = new SpeechManagerImpl(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSpeechManager.asBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
       mSpeechManager.destroy();
    }
}