package com.ragentek.homeset.core.task.foreground;


import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainType;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;


public class WechatTask extends ForegroundTask {
    private static final String TAG = "WechatTask";

    private BaseContext mBaseContext;

    public WechatTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);
        mBaseContext = baseContext;
    }

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        LogUtils.d(TAG, "onStartCommand, event="+event);
        if(event != null){
            if(event.getType() == TaskEvent.TYPE.SPEECH){
                SpeechBaseDomain speechDomain = (SpeechBaseDomain) event.getData();
                receiveSpeechEvent(speechDomain);
            }
        }
    }

    @Override
    protected boolean onSpeechEvent(SpeechBaseDomain speechDomain) {
        receiveSpeechEvent(speechDomain);
        return true;
    }

    private void receiveSpeechEvent(SpeechBaseDomain speechDomain){
        LogUtils.d(TAG, "receiveSpeechEvent, speechDomain=" + speechDomain);

        SpeechDomainType type = SpeechDomainUtils.getDomainType(speechDomain);
        if(type == SpeechDomainType.TELEPHONE){
            TaskEvent event = new TaskEvent(TaskEvent.TYPE.SPEECH, speechDomain);
            mBaseContext.startForegroundTask(LauncherTask.class, event);
        }
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
