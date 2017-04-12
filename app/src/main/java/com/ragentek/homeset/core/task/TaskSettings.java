package com.ragentek.homeset.core.task;

import android.util.SparseArray;

import com.ragentek.homeset.core.task.background.DownloadTask;
import com.ragentek.homeset.core.task.background.LoginTask;
import com.ragentek.homeset.core.task.background.WeatherUpdateTask;
import com.ragentek.homeset.core.task.foreground.LauncherTask;
import com.ragentek.homeset.core.task.foreground.SpeechTask;
import com.ragentek.homeset.core.task.foreground.TellWeatherTask;
import com.ragentek.homeset.core.task.foreground.TingTask;
import com.ragentek.homeset.core.task.foreground.WechatTask;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechDomainType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TaskSettings {
    private static final String TAG = TaskSettings.class.getSimpleName();

    private List<ForegroundTaskInfo> mForegroundTasInfoList = new LinkedList();
    private Map<Class<?>, ForegroundTaskInfo> mForegroundTaskInfoMap = new HashMap();
    private SparseArray<ForegroundTaskInfo> mForegroundTaskDomainMap = new SparseArray<ForegroundTaskInfo>();

    private List<BackgroundTaskInfo> mBackgroundTaskInfoList = new LinkedList();
    private Map<Class<?>, BackgroundTaskInfo> mBackgroundTaskInfoMap = new HashMap();

    public TaskSettings() {
        registerAllForegroundTask();
        registerAllBackgroundTask();
    }

    private void registerAllForegroundTask() {
        // TODO: register more foreground task here.
        registerLauncherTask();
        registerTingTask();
        registerWechatTask();
        registerTellWeatherTask();
        registerSpeechTask();
    }

    private void registerLauncherTask() {
        ForegroundTaskInfo taskInfo = new ForegroundTaskInfo();
        taskInfo.className = LauncherTask.class;
        taskInfo.flags |= ForegroundTaskInfo.FLAG_CLEAR_ALL;

        registerForegroundTask(taskInfo);
    }

    private void registerTingTask() {
        ForegroundTaskInfo taskInfo = new ForegroundTaskInfo();
        taskInfo.className = TingTask.class;
        taskInfo.flags = ForegroundTaskInfo.FLAG_CLEAR_ALL;
        taskInfo.domainTypes = new SpeechDomainType[] {SpeechDomainType.MUSIC,
                SpeechDomainType.HOMESET_FAVORITE, SpeechDomainType.HOMESET_CROSSTALK, SpeechDomainType.HOMESET_OPERA,
                SpeechDomainType.HOMESET_STROY, SpeechDomainType.HOMESET_HEALTH, SpeechDomainType.HOMESET_FINACE,
                SpeechDomainType.HOMESET_HISTORY, SpeechDomainType.HOMESET_RADIO};

        registerForegroundTask(taskInfo);
    }

    private void registerWechatTask() {
        ForegroundTaskInfo taskInfo = new ForegroundTaskInfo();
        taskInfo.className = WechatTask.class;
        taskInfo.domainTypes = new SpeechDomainType[] {SpeechDomainType.TELEPHONE};

        registerForegroundTask(taskInfo);
    }

    private void registerTellWeatherTask() {
        ForegroundTaskInfo taskInfo = new ForegroundTaskInfo();
        taskInfo.className = TellWeatherTask.class;
        taskInfo.domainTypes = new SpeechDomainType[] {SpeechDomainType.WEATHER};

        registerForegroundTask(taskInfo);
    }

    private void registerSpeechTask() {
        ForegroundTaskInfo taskInfo = new ForegroundTaskInfo();
        taskInfo.className = SpeechTask.class;

        registerForegroundTask(taskInfo);
    }

    protected void registerForegroundTask(ForegroundTaskInfo taskInfo) {
        Class className = taskInfo.className;
        if (findForegroundTaskInfo(className) != null) {
            throw new TaskException(className.getName() + " has already registered!");
        }

        mForegroundTasInfoList.add(taskInfo);
        mForegroundTaskInfoMap.put(className, taskInfo);

        for (SpeechDomainType domainType: taskInfo.domainTypes) {
            if (domainType.getCode() == SpeechDomainType.NULL.getCode()) {
                continue;
            }

            if (mForegroundTaskDomainMap.get(domainType.getCode()) != null) {
                dumpForegroundTaskInfoList();
                throw new TaskException("Task domain already exists, taskInfo=" + taskInfo);
            }

            mForegroundTaskDomainMap.put(domainType.getCode(), taskInfo);
        }

    }

    public ForegroundTaskInfo findForegroundTaskInfo(Class<?> className) {
        return mForegroundTaskInfoMap.get(className);
    }

    public ForegroundTaskInfo findForegroundTaskInfo(SpeechDomainType domainType) {
        return mForegroundTaskDomainMap.get(domainType.getCode());
    }

    public List<ForegroundTaskInfo> getAllForegroundTaskInfo() {
        return mForegroundTasInfoList;
    }

    private void registerAllBackgroundTask() {
        // TODO: register more background task here.
        registerLoginTask();
        registerWeatherUpdateTask();
        registerDownloadTask();
    }

    private void registerLoginTask() {
        BackgroundTaskInfo taskInfo = new BackgroundTaskInfo();
        taskInfo.className = LoginTask.class;
        taskInfo.flags |= BackgroundTaskInfo.FLAG_START_ON_BOOTUP;

        registerBackgroundTask(taskInfo);
    }

    private void registerWeatherUpdateTask() {
        BackgroundTaskInfo taskInfo = new BackgroundTaskInfo();
        taskInfo.className = WeatherUpdateTask.class;
        taskInfo.flags |= BackgroundTaskInfo.FLAG_START_ON_BOOTUP;

        registerBackgroundTask(taskInfo);
    }

    private void registerDownloadTask() {
        BackgroundTaskInfo taskInfo = new BackgroundTaskInfo();
        taskInfo.className = DownloadTask.class;

        registerBackgroundTask(taskInfo);
    }

    protected void registerBackgroundTask(BackgroundTaskInfo taskInfo) {
        Class className = taskInfo.className;
        if (findBackgroundTaskInfo(className) != null) {
            throw new TaskException(className.getName() + " has already registered!");
        }

        mBackgroundTaskInfoList.add(taskInfo);
        mBackgroundTaskInfoMap.put(className, taskInfo);
    }

    public BackgroundTaskInfo findBackgroundTaskInfo(Class<?> className) {
        return mBackgroundTaskInfoMap.get(className);
    }

    public List<BackgroundTaskInfo> getAllBackgroundTaskInfo() {
        return mBackgroundTaskInfoList;
    }

    public void dump() {
        StringBuilder builder = new StringBuilder();
        builder.append("dump [");
        builder.append("mForeTaskInfoList=").append('{').append(dumpForegroundTaskInfoList()).append('}').append(',');
        builder.append("mForeTaskDomainMap=").append(mForegroundTaskDomainMap.toString()).append(',');
        builder.append("mBackTaskInfoList").append('{').append(dumpBackgroundTaskInfoList()).append('}');
        builder.append(']');

        LogUtils.event(TAG, builder.toString());
    }

    public String dumpForegroundTaskInfoList() {
        if (mForegroundTasInfoList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for(ForegroundTaskInfo taskInfo: mForegroundTasInfoList) {
            builder.append(taskInfo);
            builder.append(',');
        }

        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    public String dumpBackgroundTaskInfoList() {
        if (mBackgroundTaskInfoList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for(BackgroundTaskInfo taskInfo: mBackgroundTaskInfoList) {
            builder.append(taskInfo);
            builder.append(',');
        }

        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }
}
