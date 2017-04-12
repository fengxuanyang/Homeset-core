package com.ragentek.homeset.core.task.foreground;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainType;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;
import com.ragentek.homeset.ui.launcher.LauncherActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LauncherTask extends ForegroundTask {
    private static final String TAG = "LauncherTask";

    private BaseContext mBaseContext;
    private Context mContext;
    private boolean mRegListener = false;

    public LauncherTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);
        mBaseContext = baseContext;
        mContext = baseContext.getAndroidContext();
    }

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        LogUtils.d(TAG, "onStartCommand, event=" + event + " mRegListener=" + mRegListener);
        if (!mRegListener) {
            EventBus.getDefault().register(this);
            mRegListener = true;
        }
        processStartCommand(event);
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    protected void onStop() {
        LogUtils.d(TAG, "onStop, mRegListener=" + mRegListener);
        if (mRegListener) {
            EventBus.getDefault().unregister(this);
            mRegListener = false;
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
    }

    private void processStartCommand(TaskEvent event) {
        LogUtils.d(TAG, "processStartCommand, event=" + event);
        Bundle bundle = new Bundle();

        if (event != null) {
            if (event.getType() == TaskEvent.TYPE.SPEECH) {
                SpeechBaseDomain speechDomain = (SpeechBaseDomain) event.getData();
                SpeechDomainType type = SpeechDomainUtils.getDomainType(speechDomain);
                if (type == SpeechDomainType.TELEPHONE) {
                    bundle.putString("operation", "call");
                }
            }
        }

        Intent intent = new Intent(mContext, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Subscribe
    public void onEventCategoryDetail(CategoryDetail categoryDetail) {
        LogUtils.d(TAG, "onEventCategoryDetail, categoryDetail=" + categoryDetail.toString());

        TaskEvent event = new TaskEvent(TaskEvent.TYPE.TOUCH, categoryDetail);
        mBaseContext.startForegroundTask(TingTask.class, event);
    }
}
