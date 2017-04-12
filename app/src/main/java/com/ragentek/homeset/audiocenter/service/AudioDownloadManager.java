package com.ragentek.homeset.audiocenter.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.ragentek.homeset.audiocenter.IDownloadService;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * Created by xuanyang.feng on 2017/3/7.
 */

public class AudioDownloadManager {
    private static AudioDownloadManager mAudioDownloadManager;
    private static final String TAG = "AudioDownloadManager";
    private IDownloadService downloadService;
    private WeakReference<Context> mcontext;

    private AudioDownloadManager(Context context) {
        mcontext = new WeakReference<Context>(context);
        Intent intent = new Intent(mcontext.get(), DownloadService.class);
        mcontext.get().startService(intent);
    }

    public static AudioDownloadManager getInstance(Context context) {
        if (mAudioDownloadManager == null) {
            synchronized (AudioDownloadManager.class) {
                if (mAudioDownloadManager == null) {
                    mAudioDownloadManager = new AudioDownloadManager(context);
                }

            }
        }
        return mAudioDownloadManager;
    }
    public   void init() {
        Intent intent = new Intent(mcontext.get(), DownloadService.class);
         mcontext.get().bindService(intent,downloadserviceconnection, Context.BIND_AUTO_CREATE);
    }

    public   void release() {
        mcontext.get().unbindService(downloadserviceconnection);
     }

    ServiceConnection downloadserviceconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected: ");
            downloadService = IDownloadService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected: ");

        }
    };

    public void startDownload(String name, String savapath, String url) {
        LogUtil.d(TAG, "startDownload: ");
        try {
            downloadService.startDowonloadFile(name,savapath,url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
