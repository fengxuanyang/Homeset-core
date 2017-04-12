package com.ragentek.homeset.core.task.handler;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.base.SpeechEngine;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.TaskManager;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.task.foreground.SpeechTask;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.IRecognitionListener;
import com.ragentek.homeset.speech.ISpeechRecognitionClient;
import com.ragentek.homeset.speech.ISpeechWakeuperClient;
import com.ragentek.homeset.speech.IWakeuperListener;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;

public class SpeechHandler {
    private static final String TAG = SpeechHandler.class.getSimpleName();
    private static final boolean DEBUG = true;

    private TaskManager mTaskManager;
    private BaseContext mBaseContext;
    private Context mContext;
    private ISpeechRecognitionClient mRecognitionClient = null;
    private ISpeechWakeuperClient mWakeupClient = null;
    private IRecognitionListener mRecognitionListener;
    private IWakeuperListener mWakeupListener;

    public SpeechHandler(TaskManager taskManager) {
        mTaskManager = taskManager;
        mBaseContext = mTaskManager.getBaseContext();
        mContext = mBaseContext.getAndroidContext();
        SpeechEngine speechEngine = (SpeechEngine) mBaseContext.getEngine(EngineManager.ENGINE_SPEECH);
        mRecognitionClient = speechEngine.getRecognitionClient();
        mWakeupClient = speechEngine.getmWakeuperClient();
        mRecognitionListener = new RecognizeListener();
        mWakeupListener = new WakeupListener();
    }

    public void startListening() {
        startWakeup();
    }

    private void startWakeup() {
        try {
            mWakeupClient.startListening(mWakeupListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class WakeupListener extends IWakeuperListener.Stub {

        @Override
        public void onResult(String result) throws RemoteException {
            wakeupScreen();
            mBaseContext.startForegroundTask(SpeechTask.class, new TaskEvent(TaskEvent.TYPE.TOUCH, null));
        }

        private void wakeupScreen() {
            PowerManager pm=(PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            if (pm.isScreenOn()) {
                return;
            }

            KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyLock = km.newKeyguardLock("unLock");
            keyLock.disableKeyguard();

            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
            wakeLock.acquire();
            wakeLock.release();
        }

        @Override
        public void onError(int errorCode, String message) throws RemoteException {
            Log.e(TAG, "wakeup error, code=" + errorCode + ", message=" + message);
        }
    }

    public void startRecognize() {
        try {
            mRecognitionClient.startRecognize(mRecognitionListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class RecognizeListener extends IRecognitionListener.Stub {

        @Override
        public void onBeginOfSpeech() throws RemoteException {

        }

        @Override
        public void onEndOfSpeech() throws RemoteException {

        }

        @Override
        public void onVolumeChanged(int value) throws RemoteException {
//            LogUtils.d(TAG, "onVolumeChanged, value=" + value);
        }

        @Override
        public void onSpeechEnd() throws RemoteException {

        }

        @Override
        public void onRecordEnd() throws RemoteException {

        }

        @Override
        public void onResult(String result) throws RemoteException {
            handleResult(result);
        }

        @Override
        public void onError(int errCode, String message) throws RemoteException {
            if (DEBUG) {
                LogUtils.d(TAG, "onError, errCode=" + errCode + ", message=" + message);
            }

            handleResult("{}");
        }
    }

    private void handleResult(String result) {
        SpeechBaseDomain speechDomain = SpeechDomainUtils.parseResult(result);
        if (DEBUG) {
            LogUtils.d(TAG, "handleResult, result=" + result);
            LogUtils.d(TAG, "handleResult, speechDomain=" + speechDomain.toString());
        }


        mTaskManager.sendSpeechEvent(speechDomain);
//        switch (SpeechDomainUtils.getDomainType(speechDomain)) {
//            case MUSIC:
//            case MUSIC_PLAYER:
//            case TELEPHONE:
//            case WEATHER:
//            case HOMESET_RADIO:
//                mTaskManager.sendSpeechEvent(speechDomain);
//                break;
//        }

        // TODO:
//        startRecognize();

        startWakeup();
    }

    public void stopListening() {
        try {
            mWakeupClient.stopListening();
            mRecognitionClient.cancelRecognize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
