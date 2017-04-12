package com.ragentek.homeset.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.task.TaskManager;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.task.foreground.LauncherTask;
import com.ragentek.homeset.core.utils.LogUtils;

public class HomesetService extends Service {
    private static final String TAG = HomesetService.class.getSimpleName();
    private static final boolean DEBUG = true;


    
    public static final String ACTION_START = "ragentek.intent.action.HOMESET_START";
    public static final String ACTION_STOP = "ragentek.intent.action.HOMESET_STOP";
    public static final String ACTION_DUMP = "ragentek.intent.action.HOMESET_DUMP";
    public static final String ACTION_DEBUG = "ragentek.intent.action.HOMESET_DEBUG";

    public static final String DEBUG_INT_EXTRA_CASE = "case";
    public static final int CASE_START_FORE_TASK = 1;
    public static final int CASE_START_BACK_TASK = 2;
    public static final int CASE_START_RECOGNITION = 3;

    public static final String DEBUG_START_RECOGNIZE = "adb am broadcast -a ragentek.intent.action.START_RECOGNITION";

    /** For instance， com.ragentek.test.MyTask*/
    public static final String DEBUG_STRING_EXTRA_TASK_NAME = "taskName";


    private EngineManager mEngineManager = null;
    private TaskManager mTaskManager = null;

    public HomesetService() { }

    @Override
    public void onCreate() {
        mEngineManager = new EngineManager(this.getApplicationContext());
        mTaskManager = new TaskManager();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = "";
        if(intent != null){
            action = intent.getAction();
            if (action == null) {
                return Service.START_FLAG_REDELIVERY;
            }
        }

        handle_action : {
            if (action.equals(ACTION_START)) {
                handleStart();
                break handle_action;
            }

            if (action.equals(ACTION_STOP)) {
                handleStop();
                stopSelf();
                break handle_action;
            }

            if (action.equals(ACTION_DUMP)) {
                handleDump();
                break handle_action;
            }

            if (action.equals(ACTION_DEBUG)) {
                handleDebug(intent);
                break handle_action;
            }
        }

        return Service.START_FLAG_REDELIVERY;
    }

    private void handleStart() {
        if (mTaskManager.isReady()) {
            mTaskManager.startForegroundTask(LauncherTask.class, new TaskEvent(TaskEvent.TYPE.TOUCH, null));
            return;
        }

        mEngineManager.init(new EngineManager.InitListener() {
            @Override
            public void onInit() {
                mTaskManager.init(mEngineManager);
            }
        });
    }

    private void handleStop() {
        if (!mTaskManager.isReady()) {
            return;
        }

        mTaskManager.release();
        mEngineManager.release();
    }

    private void handleDump() {
        if (!mTaskManager.isReady()) {
            return;
        }

        mEngineManager.dump();
        mTaskManager.dump();
    }


    private void handleDebug(Intent intent) {
        if (!DEBUG) {
            return;
        }

        if (!mTaskManager.isReady()) {
            return;
        }

        try {
            int caseId = intent.getIntExtra(DEBUG_INT_EXTRA_CASE, 0);
            switch (caseId) {
                case CASE_START_FORE_TASK:
                    String foreClassName = intent.getStringExtra(DEBUG_STRING_EXTRA_TASK_NAME);
                    Class foreClass = Class.forName(foreClassName);
                    mTaskManager.startForegroundTask(foreClass, null);
                    break;
                case CASE_START_BACK_TASK:
                    String backClassName = intent.getStringExtra(DEBUG_STRING_EXTRA_TASK_NAME);
                    Class backClass = Class.forName(backClassName);
                    mTaskManager.startBackgroundTask(backClass, null);
                    break;
                case CASE_START_RECOGNITION:
                    mTaskManager.startRecognition();
                    break;

            }
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, e.toString());
        }

    }
}
