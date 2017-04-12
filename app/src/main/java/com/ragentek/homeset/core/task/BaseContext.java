package com.ragentek.homeset.core.task;

import android.content.Context;
import android.os.Bundle;

import com.ragentek.homeset.core.base.Engine;
import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.task.event.TaskEvent;

public class BaseContext {
    private Context mAndroidContext;
    private EngineManager mEngineManager = null;
    private TaskManager mTaskManager = null;

    public BaseContext(EngineManager engineManager, TaskManager taskManager) {
        mAndroidContext = engineManager.getContext();
        mEngineManager = engineManager;
        mTaskManager = taskManager;
    }

    public Context getAndroidContext() {
        return mAndroidContext;
    }

    /**
     * Get an instance of Engine.
     * @param name please refer to EngineManager.
     */
    public Engine getEngine(String name) {
        return mEngineManager.getEngine(name);
    }

    public void startForegroundTask(Class<?> taskClass) {
        startForegroundTask(taskClass, null);
    }

    public void startForegroundTask(Class<?> taskClass, TaskEvent event) {
        mTaskManager.startForegroundTask(taskClass, event);
    }

    public void startBackgroundTask(Class<?> taskClass) {
        startBackgroundTask(taskClass, null);
    }

    public void startBackgroundTask(Class<?> taskClass, TaskEvent event) {
        mTaskManager.startBackgroundTask(taskClass, event);
    }

    public void startSystemRecognition() {
        mTaskManager.startRecognition();
    }

}
