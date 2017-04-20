package com.ragentek.homeset.audiocenter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.core.R;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public abstract class AudioToken {
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerManager;
    private String fragmentTag = this.getClass().getSimpleName();
    FragmentActivity mActivity;
    private PlayListItem currentPlayitem;
    private PlayListResultListener mPlayListResultListener = new PlayListResultListener();
    PlayBaseFragment fragment = null;

    AudioToken(FragmentActivity activity, MediaPlayerManager.MediaPlayerHandler mediaPlayer, PlayListItem item) {
        mActivity = activity;
        mediaPlayerManager = mediaPlayer;
        currentPlayitem = item;
    }


    public void show() {
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (view == null) {
            fragment = getPlayFragment();
            transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();

        } else {
            fragment = (AlbumFragment) view;
            if (!fragment.isAdded()) {
                transaction.replace(R.id.fragment_container, fragment, this.getClass().getSimpleName()).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
        getPlayList(mPlayListResultListener, currentPlayitem);
    }

    public void hide() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment view = mActivity.getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName());
        transaction.remove(view).commit();
    }

    public void playAudioSellected(int index) {
        mediaPlayerManager.play(index);
    }

    public void playOrPause() {
        mediaPlayerManager.playOrPause();
    }


    class PlayListResultListener {
        void onPlayListResult(List<PlayItem> list, int position) {
            mediaPlayerManager.setPlayList(list, position);
            fragment.setPlaydata(list);
        }
    }

    abstract PlayBaseFragment getPlayFragment();

    abstract void getPlayList(PlayListResultListener listener, PlayListItem item);

}
