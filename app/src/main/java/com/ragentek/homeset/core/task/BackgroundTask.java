package com.ragentek.homeset.core.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;

abstract public class BackgroundTask {
    private static final String TAG = BackgroundTask.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final int MSG_TASK_CREATE = 1;
    private static final int MSG_TASK_START = 2;
    private static final int MSG_TASK_STOP = 3;
    private static final int MSG_TASK_FINISH = 4;
    private static final int MSG_TASK_DESTROY = 5;

    public static final int STATE_INIT = 0;
    public static final int STATE_CREATE = 1;
    public static final int STATE_START = 2;
    public static final int STATE_STOP = 3;
    public static final int STATE_DESTROYING = 4;
    public static final int STATE_DESTROY = 5;

    private BaseContext mBaseContext;
    private HandlerThread mTaskThread;
    private TaskHandler mTaskHandler;
    private StateListener mStateListener;
    private int mState = STATE_INIT;

    public BackgroundTask(BaseContext baseContext, StateListener stateListener) {
        mBaseContext = baseContext;
        mStateListener = stateListener;
        startTaskThread();
    }

    protected BaseContext getBaseContext() {
        return mBaseContext;
    }

    private void startTaskThread() {
        mTaskThread = new HandlerThread(TAG);
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());
        sendMessage(MSG_TASK_CREATE);
    }

    public void startCommand(TaskEvent event) {
        unexpectedState(STATE_DESTROYING);
        unexpectedState(STATE_DESTROY);

        sendMessage(MSG_TASK_START, event);
    }

    public void stop() {
        unexpectedState(STATE_DESTROYING);
        unexpectedState(STATE_DESTROY);
        unexpectedState(STATE_CREATE);

        sendMessage(MSG_TASK_STOP);
    }

    public void finish() throws TaskException {
        unexpectedState(STATE_DESTROYING);
        unexpectedState(STATE_DESTROY);

        sendMessage(MSG_TASK_FINISH);
    }

    public void destroy() {
        unexpectedState(STATE_DESTROYING);
        unexpectedState(STATE_DESTROY);

        setState(STATE_DESTROYING);
        sendMessage(MSG_TASK_DESTROY);
    }

    class TaskHandler extends Handler {

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TASK_CREATE:
                    onCreate();
                    mStateListener.created(BackgroundTask.this);
                    if (getState() != STATE_DESTROYING) {
                        setState(STATE_CREATE);
                    }
                    break;
                case MSG_TASK_START:
                    onStartCommand((TaskEvent) msg.obj);
                    mStateListener.started(BackgroundTask.this);
                    setState(STATE_START);
                    break;
                case MSG_TASK_STOP:
                    if (!isTaskStarted()) {
                        return;
                    }
                    onStop();
                    mStateListener.stopped(BackgroundTask.this);
                    setState(STATE_STOP);
                    break;
                case MSG_TASK_FINISH:
                    if (!isTaskStarted()) {
                        return;
                    }

                    mStateListener.finished(BackgroundTask.this);
                    setState(STATE_STOP);
                    break;
                case MSG_TASK_DESTROY:
                    unexpectedState(STATE_START);
                    onDestroy();
                    mStateListener.destroyed(BackgroundTask.this);
                    setState(STATE_DESTROY);
                    mTaskThread.quit();
                    break;
            }
        }
    }

    protected abstract void onCreate();

    protected abstract void onStartCommand(TaskEvent event);

    protected abstract void onStop();

    protected abstract void onDestroy();

    /**
     * Be careful, these task state call back functions not run in Main thread.
     */
    public interface StateListener {
        void created(BackgroundTask task);

        void started(BackgroundTask task);

        void stopped(BackgroundTask task);

        void finished(BackgroundTask task);

        void destroyed(BackgroundTask task);
    }

    private void setState(int state) {
        mState = state;
        printLog(stateToString(state));
    }

    public int getState() {
        return mState;
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
        mTaskHandler.sendMessage(msg);
    }

    private void unexpectedState(int state) {
        if (getState() == state) {
            throw new TaskException("unexpected state " + stateToString(state));
        }
    }

    private boolean isTaskStarted() {
        return getState() == STATE_START;
    }


    private void printLog(String message) {
        if (DEBUG) {
            LogUtils.d(TAG, this.getClass().getSimpleName() + "  " + message);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(this.getClass().getSimpleName()).append(',');
        builder.append(stateToString(getState()));
        builder.append(']');
        return builder.toString();
    }

    private String stateToString(int state) {
        switch (state) {
            case STATE_INIT:
                return "TASK_INIT";
            case STATE_CREATE:
                return "TASK_CREATE";
            case STATE_START:
                return "TASK_START";
            case STATE_STOP:
                return "TASK_STOP";
            case STATE_DESTROYING:
                return "STATE_DESTROYING";
            case STATE_DESTROY:
                return "TASK_DESTROY";
        }
        return Integer.toString(state);
    }

}
