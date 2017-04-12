package com.ragentek.homeset.audiocenter.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ragentek.homeset.audiocenter.IDownloadService;
import com.ragentek.homeset.audiocenter.utils.LogUtil;


/**
 * Created by xuanyang.feng on 2017/3/2.
 */

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private Binder mBinder = new DownloadServiceBinder();
    private DownloadTaskManager mDownloadTaskManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadTaskManager = DownloadTaskManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        return mBinder;
    }

    private class DownloadServiceBinder extends IDownloadService.Stub {

        @Override
        public void startDowonloadFile(String name, String savapath, String url) throws RemoteException {
            mDownloadTaskManager.addDowonloadTask(name, savapath, url);
        }

        @Override
        public void cancelDowonloadFile(String url) throws RemoteException {
            mDownloadTaskManager.cancelDowonloadTask(url);
        }

        @Override
        public void pauseDowonloadFile(String url) throws RemoteException {
            mDownloadTaskManager.pauseDowonloadTask(url);

        }

        @Override
        public void resumeDowonloadFile(String url) throws RemoteException {
            mDownloadTaskManager.resumeDowonloadTask(url);
        }

    }


}
