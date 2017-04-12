package com.ragentek.homeset.core.task.foreground;

import android.content.Context;

import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.PlayerUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;

public class SpeechTask extends ForegroundTask {
    private static final String TAG = SpeechTask.class.getSimpleName();

    private final static String AUDIO_PATH = "audio/";
    private final static String AUDIO_WAIT = AUDIO_PATH + "tone_wait_ogg.jet";
    private final static String AUDIO_START = AUDIO_PATH + "tone_start_ogg.jet";
    private final static String AUDIO_ERROR = AUDIO_PATH + "tone_error_ogg.jet";
    private final static String AUDIO_RESULT = AUDIO_PATH + "tone_result_ogg.jet";

    private BaseContext mBaseContext;
    private Context mContext;
    private PlayerUtils mPlayer;

    public SpeechTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);
        mBaseContext = baseContext;
        mContext = baseContext.getAndroidContext();
        mPlayer = new PlayerUtils(mContext);
    }

    @Override
    public void onCreate() {

    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        mPlayer.playAssetsFile(AUDIO_START,  false, new PlayerUtils.PlayerListener() {
                    @Override
                    public void onPlayComplete() {
                        mBaseContext.startSystemRecognition();
                    }
                });
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    protected boolean onSpeechEvent(SpeechBaseDomain speechDomain) {
        mPlayer.playAssetsFile(AUDIO_RESULT,  false, new PlayerUtils.PlayerListener() {
            @Override
            public void onPlayComplete() {
                finish();
            }
        });

        return true;
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {
        mPlayer.onDestroy();
    }
}
