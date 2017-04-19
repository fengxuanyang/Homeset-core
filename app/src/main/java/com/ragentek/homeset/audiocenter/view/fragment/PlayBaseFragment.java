package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;

import com.ragentek.homeset.audiocenter.service.MyMediaPlayerControl;

/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public abstract class PlayBaseFragment<T> extends BaseFragment {
    T playdata;
    MyMediaPlayerControl control;

    public PlayBaseFragment() {

    }

    public T getPlaydata() {
        return playdata;
    }

    public void setPlaydata(T playdata) {
        this.playdata = playdata;
        if (isVisible()) {
            onDataChanged(playdata);
        }
    }

    public PlayBaseFragment(T data) {
        playdata = data;
    }

    /**
     * set the  Inner sellected of play fragment list
     *
     * @param index
     */
    public abstract void setInnerSellected(int index);

    abstract void onDataChanged(T playdata);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        control = (MyMediaPlayerControl) activity;
    }
}
