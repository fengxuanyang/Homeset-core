package com.ragentek.homeset.audiocenter;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/20.
 */

public interface IPlayListLoadListener {
    void onLoadData(int resultCode, List<PlayListItem> resultmessage);

}
