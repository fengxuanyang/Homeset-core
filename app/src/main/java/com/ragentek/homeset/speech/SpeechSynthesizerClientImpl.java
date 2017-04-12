package com.ragentek.homeset.speech;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.ragentek.homeset.speech.utils.LogUtils;

public class SpeechSynthesizerClientImpl extends ISpeechSynthesizerClient.Stub {
    private static final String TAG = SpeechSynthesizerClientImpl.class.getSimpleName();

    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认云端发音人
    public static String voicerCloud="xiaoyan";


    public SpeechSynthesizerClientImpl(Context context) {
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
    }

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                LogUtils.e(TAG, "init error, code=" + code);
            }
        }
    };

    @Override
    public void startSpeak(String text, ISynthesizerListener listener) throws RemoteException {
        setParams();
        mTts.startSpeaking(text, new TtsListener(listener));
    }

    public void setParams() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);

        //设置使用云端引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);

        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    private class TtsListener implements SynthesizerListener {
        private ISynthesizerListener mClientListener;

        public TtsListener(ISynthesizerListener mListener) {
            mClientListener = mListener;
        }

        @Override
        public void onSpeakBegin() {
            try {
                if (mClientListener != null) {
                    mClientListener.onSpeakBegin();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
        }

        @Override
        public void onSpeakPaused() {}

        @Override
        public void onSpeakResumed() {}

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            try {
                if (mClientListener != null) {
                    mClientListener.onSpeakProgress(percent, beginPos, endPos);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            try {
                if (mClientListener != null) {
                    int errorCode = 0;
                    String errorMessage = "";
                    if (speechError != null) {
                        errorCode = speechError.getErrorCode();
                        errorMessage = speechError.getMessage();
                    }
                    mClientListener.onCompleted(errorCode, errorMessage);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }

    @Override
    public void stopSpeak() throws RemoteException {
        mTts.stopSpeaking();
    }

    @Override
    public void pauseSpeak() throws RemoteException {
        mTts.pauseSpeaking();
    }

    @Override
    public void resumeSpeak() throws RemoteException {
        mTts.resumeSpeaking();
    }

    @Override
    public boolean isSpeaking() throws RemoteException {
        return mTts.isSpeaking();
    }
}
