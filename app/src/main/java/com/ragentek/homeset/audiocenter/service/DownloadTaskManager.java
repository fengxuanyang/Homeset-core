package com.ragentek.homeset.audiocenter.service;

import android.content.Context;

import com.ragentek.homeset.audiocenter.db.greendao.DownloadDBEntity;
import com.ragentek.homeset.audiocenter.utils.DatabaseManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


/**
 * Created by xuanyang.feng on 2017/3/4.
 */

public class DownloadTaskManager {
    private static final String TAG = "DownloadTaskManager";
    public static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private HashMap<Long, DownloadTask> taskMap = new HashMap();

    private static DownloadTaskManager mDownloadTaskManager;
    private ExecutorService executorService;
    private DatabaseManager mDatabaseManager;

    private DownloadTaskManager(Context context) {
        executorService = Executors.newSingleThreadExecutor();
        mDatabaseManager = DatabaseManager.getInstance(context.getApplicationContext());
    }

    public static DownloadTaskManager getInstance(Context context) {
        if (mDownloadTaskManager == null) {
            synchronized (DownloadTaskManager.class) {
                if (mDownloadTaskManager == null) {
                    mDownloadTaskManager = new DownloadTaskManager(context);
                }
            }

        }
        return mDownloadTaskManager;
    }


    public void addDowonloadTask(String name, String savapath, String url) {
        LogUtil.d(TAG, "addDowonloadTask: name:" + name + ",savapath:" + savapath + ",url:" + url);
        final long taskid = (long) url.hashCode();
        LogUtil.d(TAG, "addDowonloadTask: taskid:" + taskid);
        DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
        if (download != null) {
            mDatabaseManager.getDownloadDBEntityDao().delete(download);
        }
        DownloadDBEntity dbEntity = new DownloadDBEntity(taskid, 0l, 0l, url, savapath, name, "music", 0);
        LogUtil.d(TAG, "addDowonloadTask: taskid:" + taskid);

        //insert to db
        mDatabaseManager.getDownloadDBEntityDao().insert(dbEntity);
        DownloadTask task = new DownloadTask.Builder().setDBEntity(dbEntity).build();
        task.setmOkHttpClient(mOkHttpClient);
        taskMap.put(taskid, task);
        executorService.submit(task);
        addDowonloadListener2Task(url);
    }

    private void addDowonloadListener2Task(final String url) {
        LogUtil.d(TAG, "addDowonloadListener2Task: url:" + url);
        final long taskid = (long) url.hashCode();
        final DownloadTask task = taskMap.get(taskid);
        if (task != null) {
            task.addDownloadListener(new DownloadTask.DownloadTaskListener() {
                @Override
                public void onPrepare() {
                    LogUtil.d(TAG, "onPrepare");

                    DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
                    download.setDownloadStatus(DownloadTask.DowonloadState.DOWNLOAD_STATUS_PREPARE);
                    mDatabaseManager.getDownloadDBEntityDao().update(download);
                }

                @Override
                public void onDownloading(long totalSize, long completedSize) {
                    LogUtil.d(TAG, "onDownloading  totalSize:" + totalSize + ",completedSize:" + completedSize);

                    DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
                    download.setDownloadStatus(DownloadTask.DowonloadState.DOWNLOAD_STATUS_DOWNLOADING);
                    download.setCompletedSize(completedSize);
                    download.setToolSize(totalSize);
                    mDatabaseManager.getDownloadDBEntityDao().update(download);
                }

                @Override
                public void onPause() {
                    LogUtil.d(TAG, "onPause");

                    DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
                    download.setDownloadStatus(DownloadTask.DowonloadState.DOWNLOAD_STATUS_PAUSE);
                    mDatabaseManager.getDownloadDBEntityDao().update(download);
                    taskMap.remove(task);
                }

                @Override
                public void onCancel() {
                    LogUtil.d(TAG, "onCancel");

                    mDatabaseManager.getDownloadDBEntityDao().deleteByKey(taskid);
                    taskMap.remove(task);

                }

                @Override
                public void onCompleted() {
                    LogUtil.d(TAG, "onCompleted");

                    DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
                    download.setDownloadStatus(DownloadTask.DowonloadState.DOWNLOAD_STATUS_COMPLETED);
                    mDatabaseManager.getDownloadDBEntityDao().update(download);
                    taskMap.remove(task);
                }

                @Override
                public void onError(int errorCode) {
                    LogUtil.d(TAG, "onError errorCode:" + errorCode);

                }
            });
        } else {
            LogUtil.e(TAG, "addDowonloadListener error: taskid:" + taskid + "task is null");
        }
    }


    public void cancelDowonloadTask(String url) {
        LogUtil.d(TAG, "removeDowonloadTask: url:" + url);
        int taskid = url.hashCode();
        DownloadTask task = taskMap.get(taskid);
        if (task != null) {
            task.cancel();
        } else {
            LogUtil.e(TAG, "removeDowonloadTask: taskid:" + taskid + "task is null");
        }
    }

    public void pauseDowonloadTask(String url) {
        LogUtil.d(TAG, "pauseDowonloadTask: url:" + url);
        int taskid = url.hashCode();
        DownloadTask task = taskMap.get(taskid);
        if (task != null) {
            task.pause();
        } else {
            LogUtil.e(TAG, "pauseDowonloadTask: taskid:" + taskid + "task is null");
        }
    }

    public void resumeDowonloadTask(String url) {
        LogUtil.d(TAG, "resumeDowonloadTask: ");
        long taskid = (long) url.hashCode();
        LogUtil.d(TAG, "resumeDowonloadTask: url:" + url);
        DownloadDBEntity download = mDatabaseManager.getDownloadDBEntityDao().load(taskid);
        DownloadTask task = new DownloadTask.Builder().setDBEntity(download).build();
        task.setmOkHttpClient(mOkHttpClient);
        taskMap.put(taskid, task);
        executorService.submit(task);
    }
}
