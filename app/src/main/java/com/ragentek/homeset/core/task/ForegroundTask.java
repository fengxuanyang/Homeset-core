package com.ragentek.homeset.core.task;

import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;

/**
 * Foreground Task base class which can be only one in the running state.
 */
public abstract class ForegroundTask {
    private static final String TAG = ForegroundTask.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final int STATE_INIT = 0;
    public static final int STATE_CREATE = 1;
    public static final int STATE_START = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_RESUME = 4;
    public static final int STATE_STOP = 5;
    public static final int STATE_DESTROY = 6;

    private BaseContext mBaseContext;
    private int mState = STATE_INIT;

    public interface OnFinishListener {
        void onFinished(ForegroundTask task);
    }
    private OnFinishListener mOnFinishListener;

    public ForegroundTask(BaseContext baseContext, OnFinishListener listener) {
        mBaseContext = baseContext;
        mOnFinishListener = listener;
    }

    public void create() {
        onCreate();
        setState(STATE_CREATE);

    }

    protected BaseContext getBaseContext() {
        return mBaseContext;
    }

    public final void startCommand(TaskEvent event) {
        unexpectedState(STATE_DESTROY);

        onStartCommand(event);
        setState(STATE_START);
    }

    public final void pause() {
        unexpectedState(STATE_CREATE);
        unexpectedState(STATE_PAUSE);
        unexpectedState(STATE_STOP);
        unexpectedState(STATE_DESTROY);

        onPause();
        setState(STATE_PAUSE);
    }

    public final void resume() {
        unexpectedState(STATE_CREATE);
        unexpectedState(STATE_START);
        unexpectedState(STATE_RESUME);
        unexpectedState(STATE_STOP);
        unexpectedState(STATE_DESTROY);

        onResume();
        setState(STATE_RESUME);
    }

    public final void stop() {
        unexpectedState(STATE_CREATE);
        unexpectedState(STATE_DESTROY);

        if (getState() == STATE_STOP) {
            return;
        }

        onStop();
        setState(STATE_STOP);
    }

    public final void destroy() {
        unexpectedState(STATE_START);
        unexpectedState(STATE_PAUSE);
        unexpectedState(STATE_RESUME);
        unexpectedState(STATE_DESTROY);

        onDestroy();
        setState(STATE_DESTROY);
    }

    public final void finish() {
        unexpectedState(STATE_DESTROY);

        if (getState() == STATE_STOP) {
            return;
        }

        setState(STATE_STOP);
        mOnFinishListener.onFinished(this);
    }

    private void unexpectedState(int state) {
        if (getState() == state) {
            throw new TaskException("unexpected state " + stateToString(state));
        }
    }

    /* Use for test */
    protected final void setState(int state) {
        printLog(stateToString(state));
        mState = state;
    }

    public final int getState() {
        return mState;
    }

    abstract public void onCreate();

    abstract protected void onStartCommand(TaskEvent event);

    /**
     * Notes, if return true, you should consider to override onPause and onResume function.
     */
    abstract public boolean canPause();

    protected void onPause() {}

    protected void onResume() {}

    abstract protected void onStop();

    abstract protected void onDestroy();

    protected boolean onInterceptSpeechEvent(SpeechBaseDomain speechDomain) {
        return false;
    }

    protected boolean onSpeechEvent(SpeechBaseDomain speechDomain) {
        return false;
    }

    public void printLog(String message) {
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

    public String stateToString(int state) {
        switch (state) {
            case STATE_INIT:
                return "TASK_INIT";
            case STATE_CREATE:
                return "TASK_CREATE";
            case STATE_PAUSE:
                return "STATE_PAUSE";
            case STATE_RESUME:
                return "STATE_RESUME";
            case STATE_START:
                return "TASK_START";
            case STATE_STOP:
                return "TASK_STOP";
            case STATE_DESTROY:
                return "TASK_DESTROY";
        }
        return Integer.toString(state);
    }
}
