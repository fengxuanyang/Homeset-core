package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public abstract class BasePlayListManager {
    private static final String TAG = "BasePlayListManager";
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NET = -1;
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NONTINIT = -2;

    public static final int PLAYLISTMANAGER_RESULT_SUCCESS = 0;
    public static final int PLAYLISTMANAGER_RESULT_NONE = 1;

    TagDetail mTagDetail;
    Context mContext;
    List<PlayListItem> wholePlayList;
    int currentPage;

    PlayListManagerListener mPlayListManagerListener;
    boolean isInitted = false;

    void BasePlayListManager(TagDetail tag, Context context) {
        mTagDetail = tag;
        mContext = context;
    }

    public void init(PlayListManagerListener playListManagerListener) {
        mPlayListManagerListener = playListManagerListener;
    }

    public void update(PlayListItem playitem) {
        for (int i = 0; i < wholePlayList.size(); i++) {
            if (wholePlayList.get(i).getId() == playitem.getId()) {
                wholePlayList.set(i, playitem);
                return;
            }
        }
        LogUtil.e(TAG, " update error  do not contain: " + playitem.getId());

    }


    public void insert(int index, PlayListItem item) {
        if (wholePlayList.size() > index) {
            wholePlayList.add(index, item);
        } else {
            LogUtil.e(TAG, " insert error , do not contain  index: " + index + ",wholePlayList size is :" + wholePlayList.size());
        }
    }

    public void remove(int audioID) {
        for (PlayListItem item : wholePlayList) {
            if (item.getId() == audioID) {
                wholePlayList.remove(item);
                return;
            }
        }
        LogUtil.e(TAG, " remove error , do not contain: " + audioID);
    }

    public PlayListItem getIndexFromID(long audioid) {
        for (PlayListItem item : wholePlayList) {
            if (item.getId() == audioid) {
                return item;
            }
        }
        LogUtil.e(TAG, " getIndexFromID    , error  do not contain: " + audioid);
        return null;
    }

    abstract void loadMore();

    abstract void update2Server(PlayListItem item);

    class PlayListManagerException extends Exception {
        public PlayListManagerException(String msg) {
            super(msg);
        }

    }

}
