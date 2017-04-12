package com.ragentek.homeset.speech;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

public class SpeechWakeuperClientImpl extends ISpeechWakeuperClient.Stub {
    private static final String TAG = SpeechWakeuperClientImpl.class.getSimpleName();

    private Context mContext;
    private final static int MAX = 60;
    private final static int MIN = -20;
    private int THRESHOLD_VALUE = 10;
    private String KEEP_ALIVE = "1";
    private String IVW_NET_MODE = "0";

    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;

    public SpeechWakeuperClientImpl(Context context) {
        mContext = context;
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    @Override
    public void startListening(IWakeuperListener listener) throws RemoteException {
        setParams();
        mIvw.startListening(new VwListener(listener));
    }

    private void setParams() {
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+ THRESHOLD_VALUE);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, KEEP_ALIVE );
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, IVW_NET_MODE);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
        mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + (SpeechManagerImpl.APP_ID) + ".jet");
        Log.d(TAG, "resPath: " + resPath);
        return resPath;
    }

    private class VwListener implements WakeuperListener {
        private IWakeuperListener mClientListener;

        public VwListener(IWakeuperListener listener) {
            mClientListener = listener;
        }

        @Override
        public void onBeginOfSpeech() {}

        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            try {
                if (mClientListener != null) {
                    mClientListener.onResult(wakeuperResult.getResultString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            try {
                if (mClientListener != null) {
                    mClientListener.onError(speechError.getErrorCode(), speechError.getMessage());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        @Override
        public void onVolumeChanged(int i) {

        }
    }

    @Override
    public void stopListening() {
        mIvw.stopListening();
    }
}
