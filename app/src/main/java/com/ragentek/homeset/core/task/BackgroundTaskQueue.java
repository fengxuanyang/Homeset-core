package com.ragentek.homeset.core.task;

import android.nfc.Tag;
import android.os.Bundle;

import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BackgroundTaskQueue {
    private static final String TAG = BackgroundTaskQueue.class.getSimpleName();
    protected static final int CACHE_SIZE = 10;

    private BaseContext mBaseContext;
    private TaskSettings mSettings;
    private StateListenerImlp mBgTaskStateListener;
    protected HashMap<Class, BackgroundTask> mTaskCache = new HashMap<Class, BackgroundTask>();
    private boolean mIsRunning = false;

    public BackgroundTaskQueue(BaseContext baseContext, TaskSettings settings) {
        mBaseContext = baseContext;
        mSettings = settings;
        mBgTaskStateListener = new StateListenerImlp();
    }

    public void start() {
        if (isRunning()) {
            return;
        }

        setIsRunning(true);
        startBootupTask();
    }

    private void startBootupTask() {
        List<BackgroundTaskInfo> list = mSettings.getAllBackgroundTaskInfo();
        for (BackgroundTaskInfo taskInfo: list) {
            if ( (taskInfo.flags & BackgroundTaskInfo.FLAG_START_ON_BOOTUP) == BackgroundTaskInfo.FLAG_START_ON_BOOTUP) {
                startTask(taskInfo.className);
            }
        }
    }

    public void startTask(Class<?> taskClass) {
        startTask(taskClass, null);
    }

    public void startTask(Class<?> taskClass, TaskEvent event) {
        if (!isRunning()) {
            throw new TaskException("BackgroundTaskQueue is not running");
        }

        startTaskImpl(getTaskInfo(taskClass), event);

        LogUtils.event(TAG, "start " + taskClass.getSimpleName());
    }

    private BackgroundTaskInfo getTaskInfo(Class<?> taskClass) {
        BackgroundTaskInfo taskInfo = mSettings.findBackgroundTaskInfo(taskClass);
        if (taskInfo == null) {
            throw new TaskException("Can not found background task " +  taskClass.getName());
        }

        return taskInfo;
    }

    private void startTaskImpl(BackgroundTaskInfo taskInfo, TaskEvent event) {
        BackgroundTask task = getTaskByClass(taskInfo.className);
        task.startCommand(event);
    }

    private BackgroundTask getTaskByClass(Class taskClass) {
        BackgroundTask task = mTaskCache.get(taskClass);
        if (task == null) {
            task = newBackgroundTask(taskClass);
            mTaskCache.put(taskClass, task);
        }

        return task;
    }

    private BackgroundTask newBackgroundTask(Class taskClass) {
        Exception exception;
        try {
            Constructor constructor = taskClass.getConstructor(new Class[]{BaseContext.class, BackgroundTask.StateListener.class});
            constructor.setAccessible(true);
            BackgroundTask task = (BackgroundTask) constructor.newInstance(mBaseContext, mBgTaskStateListener);
            return task;
        } catch (NoSuchMethodException e) {
            exception = e;
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        // TODO: rename the exception.
        throw new RuntimeException("new background taskName=" + taskClass.getSimpleName() + ", error=" + exception);
    }

    class StateListenerImlp implements BackgroundTask.StateListener {

        @Override
        public void created(BackgroundTask task) {
        }

        @Override
        public void started(BackgroundTask task) {
        }

        @Override
        public void stopped(BackgroundTask task) {
        }

        @Override
        public void finished(BackgroundTask task) {
            updateTaskCache();
            LogUtils.event(TAG, "finish " + task.getClass().getSimpleName());
        }

        @Override
        public void destroyed(BackgroundTask task) {
        }
    }

    private void updateTaskCache() {
        // TODO: need to optimize
        if (mTaskCache.size() > CACHE_SIZE) {
            Iterator iterator = mTaskCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                BackgroundTask task = (BackgroundTask) pair.getValue();
                if (task.getState() == ForegroundTask.STATE_STOP) {
                    task.destroy();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Clear and destroy all back ground task.
     */
    public void stop() {
        if (!isRunning()) {
            return;
        }

        Iterator iterator = mTaskCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            BackgroundTask task = (BackgroundTask) pair.getValue();

            if (task.getState() == BackgroundTask.STATE_START) {
                task.stop();
            }

            task.destroy();
        }

        mTaskCache.clear();
        setIsRunning(false);
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    protected  void setIsRunning(boolean isRunning) {
        mIsRunning = isRunning;
    }

    public void dump() {
        StringBuilder builder = new StringBuilder();
        builder.append("dump [");
        builder.append("cache=").append('{').append(dumpTaskCache()).append('}');
        builder.append(']');

        LogUtils.event(TAG, builder.toString());
    }

    private String dumpTaskCache() {
        if (mTaskCache.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        Iterator iterator = mTaskCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            BackgroundTask task = (BackgroundTask) pair.getValue();
            builder.append(task).append(',');
        }

        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }
}
