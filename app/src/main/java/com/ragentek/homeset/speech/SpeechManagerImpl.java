package com.ragentek.homeset.speech;

import android.content.Context;
import android.os.RemoteException;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class SpeechManagerImpl extends ISpeechManager.Stub {
    public static final String APP_ID = "58a2767c";

    private Context mContext;
    private SpeechRecognitionClientImpl mRecognitionClient;
    private SpeechSynthesizerClientImpl mSynthesizerClient;
    private SpeechWakeuperClientImpl mWakeuperClient;


    public SpeechManagerImpl(Context context) {
        mContext = context;
        initEngines();
    }

    private void initEngines() {
        StringBuffer param = new StringBuffer();
        param.append("appid=" + APP_ID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(mContext, param.toString());

        mRecognitionClient = new SpeechRecognitionClientImpl(mContext);
        mSynthesizerClient = new SpeechSynthesizerClientImpl(mContext);
        mWakeuperClient = new SpeechWakeuperClientImpl(mContext);
    }

    public void destroy() {
        SpeechUtility.getUtility().destroy();
    }

    @Override
    public ISpeechRecognitionClient getRecognitionClient() throws RemoteException {
        return mRecognitionClient;
    }

    @Override
    public ISpeechSynthesizerClient getSynthesizerClient() throws RemoteException {
        return mSynthesizerClient;
    }

    @Override
    public ISpeechWakeuperClient getWakeuperClient() throws RemoteException {
        return mWakeuperClient;
    }
}