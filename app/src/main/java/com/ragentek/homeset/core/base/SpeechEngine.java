package com.ragentek.homeset.core.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ragentek.homeset.speech.ISpeechManager;
import com.ragentek.homeset.speech.ISpeechRecognitionClient;
import com.ragentek.homeset.speech.ISpeechSynthesizerClient;
import com.ragentek.homeset.speech.ISpeechWakeuperClient;
import com.ragentek.homeset.speech.SpeechService;

/**
 * Notes: get an instance of this class by calling EngineManager.getEngine(EngineManager.ENGINE_SPEECH).
 */
public class SpeechEngine extends Engine {

    private Context mContext;
    private InitListener mInitListener;

    private ISpeechWakeuperClient mWakeuperClient;
    private ISpeechRecognitionClient mRecognitionClient;
    private ISpeechSynthesizerClient mSynthesizerClient;

    private SpeechServiceConnection mSpeechServiceConnection = new SpeechServiceConnection();

    class SpeechServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {

            ISpeechManager speechManager = ISpeechManager.Stub.asInterface(service);
            try {
                mRecognitionClient = speechManager.getRecognitionClient();
                mSynthesizerClient = speechManager.getSynthesizerClient();
                mWakeuperClient = speechManager.getWakeuperClient();

                mInitListener.onInit(SpeechEngine.this, true);
            } catch (RemoteException e) {
                mInitListener.onInit(SpeechEngine.this, false);
            }
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * Construct function.
     *
     * @param name engine name.
     */
    public SpeechEngine(Context context, String name) {
        super(name);
        mContext = context;
    }

    @Override
    public void init( InitListener listener) {
        mInitListener = listener;

        Intent intent = new Intent(mContext, SpeechService.class);
        mContext.startService(intent);

        boolean success = mContext.bindService(intent, mSpeechServiceConnection, Context.BIND_AUTO_CREATE);
        if (!success) {
            mInitListener.onInit(this, false);
        }
    }

    @Override
    public void release() {
        Intent intent = new Intent(mContext, SpeechService.class);
        mContext.unbindService(mSpeechServiceConnection);
        mContext.stopService(intent);
    }

    public ISpeechWakeuperClient getmWakeuperClient() {
        return mWakeuperClient;
    }

    public ISpeechRecognitionClient getRecognitionClient() {
        return mRecognitionClient;
    }

    public ISpeechSynthesizerClient getSynthesizerClient() {
        return mSynthesizerClient;
    }

}
