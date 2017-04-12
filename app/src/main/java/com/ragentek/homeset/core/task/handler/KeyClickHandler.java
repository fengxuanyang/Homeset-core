package com.ragentek.homeset.core.task.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.TaskManager;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.task.foreground.SpeechTask;

public class KeyClickHandler {
    private BaseContext mBaseContext;
    private Context mContext;
    private OnKeyClickListener mReceiver = new OnKeyClickListener();

    public KeyClickHandler(TaskManager taskManager) {
        mBaseContext = taskManager.getBaseContext();
        mContext = mBaseContext.getAndroidContext();
    }

    public void startListening() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("ragentek.intent.action.START_RECOGNITION");
        mContext.registerReceiver(mReceiver, filter);
    }

    public void stopListening() {
        mContext.unregisterReceiver(mReceiver);
    }

    class OnKeyClickListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mBaseContext.startForegroundTask(SpeechTask.class, new TaskEvent(TaskEvent.TYPE.TOUCH, null));
        }
    }
}
