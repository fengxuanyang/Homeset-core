package com.ragentek.homeset.audiocenter;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public interface PlayListManagerListener {
    void initComplete(int resultcode, List<PlayListItem> resultmessage);

    void loadMoreComplete(int resultcode, List<PlayListItem> resultmessage);

    void onUpdate2ServerComplete(int resultcode, long audioid);

}
