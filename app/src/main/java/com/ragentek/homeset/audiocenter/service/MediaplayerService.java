/*
package com.ragentek.homeset.audiocenter.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ragentek.homeset.audiocenter.IMediaService;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.io.IOException;


public class MediaplayerService extends Service {
    private MediaSession mSession;
    private IBinder mBinder;
    private static final String TAG = "MediaplayerService";
    private XmPlayerManager mPlayerManager;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate: ");
        super.onCreate();
        mPlayerManager = XmPlayerManager.getInstance(this);
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mBinder = new ServiceStub(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.d(TAG, "onstart: ");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onunbind: ");
        return super.onUnbind(intent);
    }

    private final class ServiceStub extends IMediaService.Stub {

        public ServiceStub(MediaplayerService service) {
        }

        @Override
        public void stop() throws RemoteException {
            mPlayerManager.stop();
        }

        @Override
        public void pause() throws RemoteException {
            mPlayerManager.pause();
        }

        @Override
        public void start() throws RemoteException {
            mPlayerManager.play();
        }

        @Override
        public void setDataSource(String path) throws RemoteException {
        }
    }

    private void setupMediaSession() {
        mSession = new MediaSession(this, Constants.APP_NAME);
        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
            }

            @Override
            public void onPause() {
                super.onPause();
            }

            @Override
            public void onStop() {
                super.onStop();
            }


        });
    }

    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {
        private MediaPlayer mMediaPlayer = new MediaPlayer();
        private boolean mIsPrepared;
        private Handler handler = new Handler();

        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtil.d(TAG, "onCompletion: ");
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        public void setDataSource(final String path) {
            LogUtil.d(TAG, "setDataSource path:  " + path);
            try {
                mMediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.prepareAsync();
        }

        public void start() {
            LogUtil.d(TAG, "start  mIsPrepared: " + mIsPrepared);
            if (mIsPrepared) {
                mMediaPlayer.start();
            } else {
                handler.postDelayed(startMediaPlayerIfPrepared, 500);
            }

        }

        public void stop() {
            LogUtil.d(TAG, "stop: ");
            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mMediaPlayer.reset();
            mIsPrepared = false;
        }


        public void release() {
            LogUtil.d(TAG, "release: ");
            mMediaPlayer.release();
        }


        public void pause() {
            LogUtil.d(TAG, "player_play: ");
            handler.removeCallbacks(startMediaPlayerIfPrepared);
            mMediaPlayer.pause();
        }

        Runnable startMediaPlayerIfPrepared = new Runnable() {
            @Override
            public void run() {
                start();
            }
        };

        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.d(TAG, "onPrepared");
                mp.setOnCompletionListener(MultiPlayer.this);
                mIsPrepared = true;
            }
        };
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {

        }

        @Override
        public void onSoundPrepared() {
        }

        @Override
        public void onSoundPlayComplete() {
        }

        @Override
        public void onPlayStop() {
        }

        @Override
        public void onPlayStart() {
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        @Override
        public void onPlayPause() {
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            return false;

        }

        @Override
        public void onBufferingStop() {
        }

        @Override
        public void onBufferingStart() {
        }

        @Override
        public void onBufferProgress(int percent) {
        }

    };
}

*/