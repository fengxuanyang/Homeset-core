package com.ragentek.homeset.audiocenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;


public class MediaService extends Service {
    private static final String TAG = "MediaService";

    private final String XMLY_APPSECRET = "c8305c13038e87298c9bc2bd1aa5b116 ";
    private XmPlayerManager mPlayerManager;
    private CommonRequest mXimalaya;
    private MediaServiceStub mMediaServiceStub;
    private Context mContext;
    private boolean mInitComplete;
    private int currentPlayIndxt;
    private List<Track> currentPlayList = new ArrayList<>();
    private IMediaPlayerListener mMediaPlayerListener;
    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.d(TAG, "onSoundPrepared");
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onSoundPrepared();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mPlayerManager.play();
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
        }

        @Override
        public void onPlayStop() {
            Log.d(TAG, "onPlayStop:");
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onPlayStop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPlayStart() {
            Log.d(TAG, "onPlayStart:");
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onPlayStart();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            Log.d(TAG, "onPlayProgress:" + currPos + ",duration" + duration + " mMediaPlayerListener=" + mMediaPlayerListener);
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onPlayProgress(currPos, duration);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPlayPause() {
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onPlayStop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSoundPlayComplete() {
            Log.d(TAG, "onSoundPlayComplete");
            if (mMediaPlayerListener != null) {
                try {
                    mMediaPlayerListener.onSoundPlayComplete();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.e(TAG, "onError" + exception.getMessage());
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            Log.d(TAG, "onBufferProgress :" + position);
        }

        public void onBufferingStart() {
            Log.d(TAG, "onBufferingStart");
        }

        public void onBufferingStop() {
            Log.d(TAG, "onBufferingStop");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = getApplicationContext();
        mMediaServiceStub = new MediaServiceStub(this);
        mXimalaya = CommonRequest.getInstanse();
        mXimalaya.init(mContext, XMLY_APPSECRET);
        mInitComplete = false;
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        mPlayerManager = XmPlayerManager.getInstance(mContext);
        mPlayerManager.setPlayListChangeListener(new IXmDataCallback() {
            @Override
            public void onDataReady(List<Track> list, boolean b, boolean b1) throws RemoteException {
                Log.d(TAG, "onDataReady: " + list.size() + ",b:" + b + ",b1:" + b1);
            }

            @Override
            public void onError(int i, String s, boolean b) throws RemoteException {
                Log.e(TAG, "onError: " + s + ",b:" + b + ",i:" + i);
            }

            @Override
            public IBinder asBinder() {
                Log.d(TAG, "asBinder: ");
                return null;
            }
        });
        mPlayerManager.init();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.setOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mXimalaya.setDefaultPagesize(50);
                Log.d(TAG, "onConnected: " + Thread.currentThread().getName());

                mInitComplete = true;

                if (mMediaPlayerListener != null) {
                    try {
                        mMediaPlayerListener.initComplete();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void release() {
        Log.d(TAG, "release");
        if (mPlayerManager != null) {
            mPlayerManager.resetPlayList();
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
            mPlayerManager.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand, intent=" + intent + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind, intent=" + intent);
        release();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind, intent=" + intent);
        return mMediaServiceStub;
    }

    private static class MediaServiceStub extends IMediaService.Stub {
        MediaService mService;

        public MediaServiceStub(MediaService mService) {
            this.mService = mService;
        }

        @Override
        public void addMediaPlayerListener(IMediaPlayerListener listener) throws RemoteException {
            mService.addMediaPlayerListener(listener);
        }

        @Override
        public void addPlayList(List<MyTrack> list, int startIndex) throws RemoteException {
            mService.addPlayList(list);
        }

        @Override
        public void setPlayList(List<MyTrack> list, int startIndex) throws RemoteException {
            mService.setPlayList(list, startIndex);
        }

        @Override
        public List<MyTrack> getPlayList() throws RemoteException {
            return mService.getPlayList();
        }


        @Override
        public void play(int index) throws RemoteException {
            mService.play(index);

        }


        @Override
        public void playNext() throws RemoteException {
            mService.playNext();
        }

        @Override
        public void playPre() throws RemoteException {
            mService.playPre();
        }

        @Override
        public void startOrPause() throws RemoteException {
            mService.startOrPause();
        }


        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.isPlaying();
        }

        @Override
        public void seekToByPercent(float percent) throws RemoteException {
            mService.seekToByPercent(percent);
        }

        @Override
        public void clearPlayList() throws RemoteException {
            mService.resetPlayList();
        }
    }

    private void addMediaPlayerListener(IMediaPlayerListener listener) {
        Log.d(TAG, "addMediaPlayerListener, listener=" + listener + " mInitComplete=" + mInitComplete);

        mMediaPlayerListener = listener;
        if (listener != null) {
            if (mInitComplete) {
                try {
                    listener.initComplete();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param list
     */
    private void addPlayList(List<MyTrack> list) {
        Log.d(TAG, "addPlayList");
        if (list != null) {
            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Track track = list.get(i).getTrack();
                tracks.add(track);
            }
            if (currentPlayList == null) {
                currentPlayList = tracks;
            } else {
                currentPlayList.addAll(tracks);
            }
        }
    }

    private void setPlayList(List<MyTrack> list, int startIndex) {
        Log.d(TAG, "setPlayList, list=" + list + " startIndex=" + startIndex);
        if (list != null) {
            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Track track = list.get(i).getTrack();
                tracks.add(track);
            }
            currentPlayList = tracks;
            play(startIndex);
        }
    }

    private List<MyTrack> getPlayList() {
        Log.d(TAG, "getPlayList");
        List<MyTrack> list = new ArrayList<>();
        List<Track> tracks = mPlayerManager.getPlayList();
        if (tracks != null) {
            for (int i = 0; i < tracks.size(); i++) {
                MyTrack myTrack = new MyTrack();
                myTrack.setTrack(tracks.get(i));
                list.add(myTrack);
            }
        }

        return list;
    }

    private void resetPlayList() {
        Log.d(TAG, "resetPlayList");
        mPlayerManager.resetPlayList();
        mPlayerManager.stop();
        currentPlayList.clear();
    }

    private void play(int index) {
        Log.d(TAG, "play");
        List<Track> templayList = new ArrayList<>();
        mPlayerManager.resetPlayList();
        templayList.add(currentPlayList.get(index));
        mPlayerManager.setPlayList(templayList, 0);
        currentPlayIndxt = index;
    }

    private void playNext() {
        Log.d(TAG, "playNext");
        if (currentPlayIndxt < currentPlayList.size()) {
            currentPlayIndxt++;
        }
        play(currentPlayIndxt);
    }

    private void playPre() {
        Log.d(TAG, "playPre");
        Log.d(TAG, "playNext");
        if (currentPlayIndxt > 1) {
            currentPlayIndxt--;
        }
        play(currentPlayIndxt);
    }

    private void startOrPause() {
        Log.d(TAG, "startOrPause");
        if (mPlayerManager.isPlaying()) {
            mPlayerManager.pause();
        } else {
            mPlayerManager.play();
        }
    }


    private boolean isPlaying() {
        Log.d(TAG, "isPlaying");
        return mPlayerManager.isPlaying();
    }

    private void seekToByPercent(float percent) {
        Log.d(TAG, "seekToByPercent, percent=" + percent);
        mPlayerManager.seekToByPercent(percent);
    }
}