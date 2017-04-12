package com.ragentek.homeset.core.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlayerUtils implements MediaPlayer.OnCompletionListener {
    private final String TAG = "SpeechPlayer";

    private Context mContext;
    private MediaPlayer mPlayer = new MediaPlayer();
    private AssetManager mAssetManager;

    public interface PlayerListener {
        public void onPlayComplete();
    }
    private PlayerListener mPlayListener;

    public PlayerUtils(Context context) {
        mContext = context;
        mAssetManager = context.getAssets();
        mPlayer.setOnCompletionListener(this);
    }

    public void playAssetsFile(String file, boolean repeat, PlayerListener listener) {
        mPlayListener = listener;
        playAssetsFile(file, repeat);
    }

    private void playAssetsFile(String file, boolean repeat){
        try
        {
            AssetFileDescriptor fd = mAssetManager.openFd(file);
            mPlayer.reset();
            mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            fd.close();

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mPlayer.prepare();
            mPlayer.setLooping(repeat);
            mPlayer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopPlay()
    {
        if (mPlayer != null && mPlayer.isPlaying()){
            mPlayListener = null;
            mPlayer.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mPlayListener != null){
            mPlayListener.onPlayComplete();
        }
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void onDestroy()
    {
        mPlayer.release();
        mPlayer = null;
    }
}