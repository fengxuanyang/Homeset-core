package com.ragentek.homeset.audiocenter.service;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/3/22.
 */

public interface MyMediaPlayerControl {
    void play(int position);

    void setPlayList(List<PlayItem> list, int position);

    void addPlayList(List<PlayItem> list, int position);

}
