package com.ragentek.homeset.core.task;

import android.os.Handler;
import android.os.Message;

import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.task.handler.ConnectivityHandler;
import com.ragentek.homeset.core.task.handler.KeyClickHandler;
import com.ragentek.homeset.core.task.handler.SpeechHandler;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;

public class TaskManager {
    private static final String TAG = TaskManager.class.getSimpleName();
    private static  final boolean DEBUG = true;

    private static final int MSG_START_FOREGROUND_TASK = 1;
    private static final int MSG_START_BACKGROUND_TASK = 2;
    private static final int MSG_SPEECH_EVENT = 3;

    private class EventObject {
        public Object obj1;
        public Object obj2;
    }

    private BaseContext mBaseContext;
    private boolean mIsReady = false;

    private TaskSettings mSettings;
    private WorkHandler mWorkHandler = null;

    private ForegroundTaskStack mForegroundTaskStack = null;
    private BackgroundTaskQueue mBackgroundTaskQueue = null;

    private SpeechHandler mSpeechHandler;
    private ConnectivityHandler mConnectivityHandler;
    private KeyClickHandler mKeyClickHandler;

    public TaskManager() {
    }

    public void init(EngineManager engineManager) {
        if (isReady()) {
            return;
        }

        mBaseContext = new BaseContext(engineManager, this);
        mSettings = new TaskSettings();
        mWorkHandler = new WorkHandler();

        mForegroundTaskStack = new ForegroundTaskStack(mBaseContext, mSettings);
        mBackgroundTaskQueue = new BackgroundTaskQueue(mBaseContext, mSettings);
        mForegroundTaskStack.start();
        mBackgroundTaskQueue.start();

        mSpeechHandler = new SpeechHandler(this);
        mSpeechHandler.startListening();

        mConnectivityHandler = new ConnectivityHandler(this);
        mConnectivityHandler.startListening();

        mKeyClickHandler = new KeyClickHandler(this);
        mKeyClickHandler.startListening();

        setIsReady(true);
    }

    public void release() {
        if (!isReady()) {
            return;
        }

        mForegroundTaskStack.stop();
        mBackgroundTaskQueue.stop();

        mConnectivityHandler.stopListening();
        mKeyClickHandler.stopListening();
        mSpeechHandler.stopListening();

        setIsReady(false);
    }

    // TODO: use for speech test
    public void startRecognition() {
        mSpeechHandler.startRecognize();
    }

    public BaseContext getBaseContext() {
        return mBaseContext;
    }

    public void startForegroundTask(Class<?> taskClass, TaskEvent event) {
        EventObject msgObject = new EventObject();
        msgObject.obj1 = taskClass;
        msgObject.obj2 = event;

        sendMessage(MSG_START_FOREGROUND_TASK, msgObject);
    }

    public void startBackgroundTask(Class<?> taskClass, TaskEvent event) {
        EventObject msgObject = new EventObject();
        msgObject.obj1 = taskClass;
        msgObject.obj2 = event;

        sendMessage(MSG_START_BACKGROUND_TASK, msgObject);
    }

    public void sendSpeechEvent(SpeechBaseDomain speechDomain) {
        sendMessage(MSG_SPEECH_EVENT, speechDomain);
    }

    protected class WorkHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_FOREGROUND_TASK:
                    handleStartForegroundTaskEvent((EventObject) msg.obj);
                    break;
                case MSG_START_BACKGROUND_TASK:
                    handleStartBackgroundTaskEvent((EventObject) msg.obj);
                    break;
                case MSG_SPEECH_EVENT:
                    handleSpeechEvent((SpeechBaseDomain) msg.obj);
                    break;
            }
        }
    }

    private void handleStartForegroundTaskEvent(EventObject msgObject) {
        Class<?> taskClass = (Class<?>) msgObject.obj1;
        TaskEvent event = (TaskEvent) msgObject.obj2;
        mForegroundTaskStack.startTask(taskClass, event);
    }

    private void handleStartBackgroundTaskEvent(EventObject msgObject) {
        Class<?> taskClass = (Class<?>) msgObject.obj1;
        TaskEvent event = (TaskEvent) msgObject.obj2;
        mBackgroundTaskQueue.startTask(taskClass, event);
    }

    private void handleSpeechEvent(SpeechBaseDomain speechDomain) {
        mForegroundTaskStack.sendSpeechEvent(speechDomain);
    }

    private void setIsReady(boolean isReady) {
        mIsReady = isReady;

        LogUtils.event(TAG, "isReady =" + isReady);
    }

    public boolean isReady() {
        return mIsReady;
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mWorkHandler.sendMessage(msg);
    }

    public void dump() {
        mSettings.dump();
        mForegroundTaskStack.dump();
        mBackgroundTaskQueue.dump();
    }

    public void printEventLog(String message) {
        LogUtils.event(TAG, message);
    }

    public void printLog(String message) {
        if (DEBUG) {
            LogUtils.d(TAG, message);
        }
    }
}
