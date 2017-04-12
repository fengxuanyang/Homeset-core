package com.ragentek.homeset.core.task;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ragentek.homeset.core.task.ForegroundTask.OnFinishListener;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.task.foreground.LauncherTask;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainType;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class ForegroundTaskStack {
    private static final String TAG = ForegroundTaskStack.class.getSimpleName();

    /* Message type */
    private static final int MSG_TASK_START = 1;
    private static final int MSG_TASK_FINISHED = 2;
    private static final int MSG_STOP = 3;
    private static final int MSG_SPEECH_EVENT = 4;

    /* The maximum value of cache size */
    private static final int CACHE_SIZE = 10;

    /* State enum */
    public static final int STATE_START = 1;
    public static final int STATE_STOP = 2;

    private BaseContext mBaseContext;
    private TaskSettings mSettings;

    private HandlerThread mNewThread;
    private WorkHandler mWorkHandler;
    private int mState;
    private OnFinishListener mOnTaskFinished =  new ForegroundTaskFinishListener();

    /* Task cache map */
    private HashMap<Class, ForegroundTask> mTaskCache = new HashMap<Class, ForegroundTask>();

    protected class MsgObject {
        protected ForegroundTaskInfo taskInfo;
        protected TaskEvent event;

        public MsgObject(ForegroundTaskInfo taskClass, TaskEvent event) {
            this.taskInfo = taskClass;
            this.event = event;
        }
    }

    protected class TaskRecorder {
        protected ForegroundTaskInfo taskInfo;
        protected ForegroundTask task;
    }
    private Stack<TaskRecorder> mTaskStack = new Stack<TaskRecorder>();

    public ForegroundTaskStack(BaseContext baseContext, TaskSettings settings) {
        mBaseContext = baseContext;
        mSettings = settings;
    }

    public void start() {
        startNewThread();
        startDefaultTask();
    }

    private void startNewThread() {
        mNewThread = new HandlerThread(TAG);
        mNewThread.start();
        mWorkHandler = new WorkHandler(mNewThread.getLooper());
        setState(STATE_START);
    }

    public void startTask(Class<?> taskClass, TaskEvent event) {
        if (getState() != STATE_START) {
            throw new RuntimeException("can not start task, because ForegroundTaskStack is already stopped!");
        }

        ForegroundTaskInfo taskInfo = getTaskInfo(taskClass);
        sendMessage(MSG_TASK_START, new MsgObject(taskInfo, event));
    }

    private ForegroundTaskInfo getTaskInfo(Class<?> taskClass) {
        ForegroundTaskInfo taskInfo = mSettings.findForegroundTaskInfo(taskClass);
        if (taskInfo == null) {
            throw new TaskException("Can not found task " + taskClass.getName());
        }

        return taskInfo;
    }

    class ForegroundTaskFinishListener implements ForegroundTask.OnFinishListener {

        @Override
        public void onFinished(ForegroundTask task) {
            sendMessage(MSG_TASK_FINISHED, task);
        }
    }

    public void stop() {
        if (getState() == STATE_STOP) {
            return;
        }

        sendMessage(MSG_STOP);
    }

    public void sendSpeechEvent(SpeechBaseDomain speechDomain) {
        sendMessage(MSG_SPEECH_EVENT, speechDomain);
    }

    class WorkHandler extends Handler {

        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TASK_START:
                    MsgObject msgObject = (MsgObject) msg.obj;
                    handleTaskStart(msgObject);
                    break;
                case MSG_TASK_FINISHED:
                    ForegroundTask task = (ForegroundTask) msg.obj;
                    handleTaskFinished(task);
                    break;
                case MSG_STOP:
                    handleStop();
                    break;
                case MSG_SPEECH_EVENT:
                    handleSpeechEvent((SpeechBaseDomain) msg.obj);
                    break;
            }
        }
    }

    private void handleTaskStart(MsgObject msgObject) {
        goto_end: {
            TaskRecorder topTaskRecorder = peakTopTaskRecorder();
            if(topTaskRecorder == null) {
                startAndPushStack(msgObject);
                break goto_end;
            }

            if (topTaskRecorder.taskInfo.className.equals(msgObject.taskInfo.className)) {
                restartTopTask(msgObject);
                break goto_end;
            }

            if (msgObject.taskInfo.priority < topTaskRecorder.taskInfo.priority) {
                // TODO:
                break goto_end;
            }

            startTask(msgObject);
        }
    }

    private void restartTopTask(MsgObject msgObject) {
        peakTopTask().startCommand(msgObject.event);

        LogUtils.event(TAG, "restart " + msgObject.taskInfo.className.getSimpleName() + ", event=" + msgObject.event);
    }

    private void startTask(MsgObject msgObject) {
        if ((msgObject.taskInfo.flags & ForegroundTaskInfo.FLAG_CLEAR_ALL) == ForegroundTaskInfo.FLAG_CLEAR_ALL) {
            stopAndClearAllTask();
        }

        if (peakTopTask() != null) {
            if (peakTopTask().canPause()) {
                pauseTopTask();
            } else {
                stopAndPopTopTask();
            }
        }

        startAndPushStack(msgObject);
    }

    private void stopAndClearAllTask() {
        while (!isTaskStackEmpty()) {
            TaskRecorder taskRecorder = mTaskStack.pop();
            taskRecorder.task.stop();
        }

        LogUtils.event(TAG, "clear all task");
    }

    private void pauseTopTask() {
        ForegroundTask topTask = peakTopTask();
        topTask.pause();

        LogUtils.event(TAG, "pause " + topTask.getClass().getSimpleName());
    }

    private void stopAndPopTopTask() {
        ForegroundTask topTask = peakTopTask();
        topTask.stop();
        mTaskStack.pop();

        LogUtils.event(TAG, "stop " + topTask.getClass().getSimpleName());
    }

    private void startAndPushStack(MsgObject msgObject) {
        ForegroundTask task = getTaskByClass(msgObject.taskInfo.className);
        task.startCommand(msgObject.event);

        TaskRecorder taskRecorder = new TaskRecorder();
        taskRecorder.taskInfo = msgObject.taskInfo;
        taskRecorder.task = task;
        mTaskStack.push(taskRecorder);

        LogUtils.event(TAG, "start " + peakTopTask().getClass().getSimpleName() + ", taskEvent=" + msgObject.event);
    }

    private ForegroundTask getTaskByClass(Class taskClass) {
        ForegroundTask task = mTaskCache.get(taskClass);
        if (task == null) {
            task = newForegroundTask(taskClass);
            task.create();
            mTaskCache.put(taskClass, task);
        }

        return task;
    }

    private ForegroundTask newForegroundTask(Class taskClass) {
        Exception exception;
        try {
            Constructor constructor = taskClass.getConstructor(new Class[] {BaseContext.class, OnFinishListener.class});
            constructor.setAccessible(true);
            ForegroundTask task = (ForegroundTask) constructor.newInstance(mBaseContext, mOnTaskFinished);
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
        throw new TaskException("new foreground taskName=" + taskClass.getSimpleName() + ", error=" + exception);
    }

    private void handleTaskFinished(ForegroundTask task) {
        LogUtils.event(TAG, "finish " + task.getClass().getSimpleName());

        ForegroundTask topTask = peakTopTask();
        if (task == null) {
            return;
        }

        if (task != topTask) {
            removeTaskFromStackIfFound(task);
            return;
        }

        mTaskStack.pop();
        if (isTaskStackEmpty()) {
            startDefaultTask();
            return;
        }

        peakTopTask().resume();

        updateTaskCache();

        LogUtils.event(TAG, "resume " + peakTopTask().getClass().getSimpleName());
    }

    private void removeTaskFromStackIfFound(ForegroundTask finishedTask) {
        Iterator<TaskRecorder> iterator = mTaskStack.iterator();
        if (iterator.hasNext()) {
            TaskRecorder recorder = iterator.next();
            if (recorder.task == finishedTask) {
                iterator.remove();
                return;
            }
        }
    }

    private void startDefaultTask() {
        startTask(LauncherTask.class, null);
    }

    private void updateTaskCache() {
        // TODO: need to optimize
        if (mTaskCache.size() > CACHE_SIZE) {
            Iterator iterator = mTaskCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                ForegroundTask task = (ForegroundTask) pair.getValue();
                if (task.getState() == ForegroundTask.STATE_STOP) {
                    task.destroy();
                    iterator.remove();
                }
            }
        }
    }

    private void handleStop() {
        clearTaskStack();
        clearCacheTask();
        mNewThread.quitSafely();
        setState(STATE_STOP);
    }

    private void clearTaskStack() {
        while(!isTaskStackEmpty()) {
            TaskRecorder recorder = mTaskStack.pop();
            recorder.task.stop();
        }
    }

    private void clearCacheTask() {
        Iterator iterator = mTaskCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ForegroundTask task = (ForegroundTask) pair.getValue();
            task.destroy();
        }
        mTaskCache.clear();
    }

    private void handleSpeechEvent(SpeechBaseDomain speechDomain) {
        ForegroundTask task = peakTopTask();
        if (task != null) {
            if (task.onInterceptSpeechEvent(speechDomain)) {
                LogUtils.event(TAG, "onInterceptSpeechEvent, task=" + task.getClass().getSimpleName() + ", speechDomain=" + speechDomain);
                return;
            }
        }

        if (handleTaskSwitch(speechDomain)) {
            return;
        }

        if (task != null) {
            LogUtils.event(TAG, "onSpeechEvent, task=" + task.getClass().getSimpleName() + ", speechDomain=" + speechDomain);
            task.onSpeechEvent(speechDomain);
        }
    }

    private boolean handleTaskSwitch(SpeechBaseDomain domain) {
        SpeechDomainType domainType = SpeechDomainUtils.getDomainType(domain);
        ForegroundTaskInfo taskInfo = mSettings.findForegroundTaskInfo(domainType);
        if (taskInfo == null) {
            return false;
        }

        TaskRecorder topTaskRecorder = peakTopTaskRecorder();
        if (Arrays.asList(topTaskRecorder.taskInfo.domainTypes).contains(domainType)) {
            return false;
        }

        startTask(taskInfo.className, new TaskEvent(TaskEvent.TYPE.SPEECH,domain));
        return true;
    }

    protected ForegroundTask peakTopTask() {
        TaskRecorder recorder = peakTopTaskRecorder();
        if(recorder == null) {
            return null;
        }
        return recorder.task;
    }

    protected TaskRecorder peakTopTaskRecorder() {
        if(isTaskStackEmpty()) {
            return null;
        }
        return mTaskStack.peek();
    }

    protected boolean isTaskStackEmpty() {
        return mTaskStack.isEmpty();
    }

    private void sendMessage(int what) {
        sendMessage(what, null);
    }

    private void sendMessage(int what, Object obj) {
        sendMessage(what, obj, 0);
    }

    private void sendMessage(int what, Object obj, int arg1) {
        sendMessage(what, obj, arg1, 0);
    }

    private void sendMessage(int what, Object obj, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        mWorkHandler.sendMessage(msg);
    }

    private void setState(int state) {
        mState = state;
    }

    /* Using for unit test */
    protected int getState() {
        return mState;
    }

    /* Using for unit test */
    protected Stack<TaskRecorder> getTaskStack() {
        return mTaskStack;
    }

    /* Using for unit test */
    protected HashMap<Class, ForegroundTask> getTaskCache() {
        return mTaskCache;
    }

    public void dump() {
        StringBuilder builder = new StringBuilder();
        builder.append("dump [");
        builder.append(stateToString(getState())).append(',');
        builder.append("stack=").append('{').append(dumpTaskStack()).append('}').append(',');
        builder.append("cache=").append('{').append(dumpTaskCache()).append('}');
        builder.append(']');

        LogUtils.event(TAG, builder.toString());
    }

    private String dumpTaskStack() {
        if (mTaskStack.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for(TaskRecorder recorder: mTaskStack) {
            builder.append('[');
            builder.append("task=").append(recorder.task);
            builder.append(']');
            builder.append(',');
        }

        builder.delete(builder.length() - 1, builder.length());
        return  builder.toString();
    }

    private String dumpTaskCache() {
        if (mTaskCache.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        Iterator iterator = mTaskCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ForegroundTask task = (ForegroundTask) pair.getValue();
            builder.append(task).append(',');
        }

        builder.delete(builder.length()-1, builder.length());
        return  builder.toString();
    }

    public String stateToString(int state) {
        switch (state) {
            case STATE_START:
                return "TASK_START";
            case STATE_STOP:
                return "TASK_STOP";
        }
        return Integer.toString(state);
    }
}
