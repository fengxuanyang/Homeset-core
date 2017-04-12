package com.ragentek.homeset.core.task.background;

import com.ragentek.homeset.core.task.BackgroundTask;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.event.TaskEvent;

public class DownloadTask extends BackgroundTask {

    public DownloadTask(BaseContext baseContext, StateListener stateListener) {
        super(baseContext, stateListener);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStartCommand(TaskEvent event) {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
