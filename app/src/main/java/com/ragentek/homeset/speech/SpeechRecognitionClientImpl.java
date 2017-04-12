package com.ragentek.homeset.speech;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.ragentek.homeset.speech.utils.LogUtils;

public class SpeechRecognitionClientImpl extends ISpeechRecognitionClient.Stub {
    private static final String TAG = SpeechRecognitionClientImpl.class.getSimpleName();

    private Context mContext;
    private SpeechUnderstander mSpeechUnderstander;

    public SpeechRecognitionClientImpl(Context context) {
        mContext = context;
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, mInitListener);
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                LogUtils.e(TAG, "init error, code=" + code);
            }
        }
    };

    @Override
    public void startRecognize(IRecognitionListener listener) throws RemoteException {
        setParams();
        mSpeechUnderstander.startUnderstanding(new UnderstanderListener(listener));
    }

    private void setParams() {
        // 设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT,"mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
    }

    @Override
    public void stopRecognize() throws RemoteException {
        mSpeechUnderstander.stopUnderstanding();
    }

    @Override
    public void cancelRecognize() throws RemoteException {
        mSpeechUnderstander.cancel();
    }

    private class UnderstanderListener implements SpeechUnderstanderListener {
        private IRecognitionListener mClientListener;

        public UnderstanderListener(IRecognitionListener listener) {
            mClientListener = listener;
        }

        @Override
        public void onResult(final UnderstanderResult result) {
            try {
                if (mClientListener != null) {
                    mClientListener.onResult(result.getResultString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            try {
                if (mClientListener != null) {
                    mClientListener.onVolumeChanged(volume);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEndOfSpeech() {
            try {
                if (mClientListener != null) {
                    mClientListener.onEndOfSpeech();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBeginOfSpeech() {
            try {
                if (mClientListener != null) {
                    mClientListener.onBeginOfSpeech();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            try {
                if (mClientListener != null) {
                    mClientListener.onError(error.getErrorCode(), error.getMessage());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

}
