package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.util.List;

import rx.Subscriber;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public abstract class BasePlayListToken {
    private static final String TAG = "BasePlayListToken";
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NET = -1;
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NONTINIT = -2;

    public static final int PLAYLISTMANAGER_RESULT_SUCCESS = 0;
    public static final int PLAYLISTMANAGER_RESULT_NONE = 1;

    TagDetail mTagDetail;
    Context mContext;
    List<PlayListItem> wholePlayList;
    int currentPlayIndext = 1;

    PlayListManagerListener mPlayListManagerListener;
    boolean isInitted = false;
    int currentPage = 1;

    public BasePlayListToken(TagDetail tag, Context context) {
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

    public PlayListItem getPlayListItemFromID(long audioid) {
        for (PlayListItem item : wholePlayList) {
            if (item.getId() == audioid) {
                return item;
            }
        }
        LogUtil.e(TAG, " getIndexFromID    , error  do not contain: " + audioid);
        return null;
    }

    public PlayListItem getPlayListItemFromIndext(int index) {
        if (index > -1 && index < wholePlayList.size()) {
            return wholePlayList.get(index);
        }

        LogUtil.e(TAG, " getIndexFromID    , error  do not contain  index: " + index);
        return null;
    }


    public List<PlayListItem> gePlayList() {
        return wholePlayList;
    }

    void requestPlayDataComplete(int resultCode, List<PlayListItem> resultmessage) {
        if (!isInitted) {
            mPlayListManagerListener.initComplete(resultCode, resultmessage);
            if (resultCode == PLAYLISTMANAGER_RESULT_SUCCESS) {
                wholePlayList = resultmessage;
                isInitted = true;
            }
        } else {
            mPlayListManagerListener.loadMoreComplete(resultCode, resultmessage);
            if (resultCode == PLAYLISTMANAGER_RESULT_SUCCESS) {
                wholePlayList.addAll(resultmessage);
            }
        }
    }


    public void updateFav2Server(final long audioid) {
        int result = isCurrentPlaylistContain(audioid);
        if (result != -1) {
            final PlayListItem item2BeChanged = wholePlayList.get(result);
            Subscriber<String> mSetFavSubscriber = new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    LogUtil.d(TAG, "onNext onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.e(TAG, "onNext result: " + e.getMessage());
                    mPlayListManagerListener.onUpdate2ServerComplete(PLAYLISTMANAGER_RESULT_ERROR_NET, audioid);
                }

                @Override
                public void onNext(String result) {
                    LogUtil.d(TAG, "onNext result: " + result);
                    item2BeChanged.updateFav();
                    mPlayListManagerListener.onUpdate2ServerComplete(PLAYLISTMANAGER_RESULT_SUCCESS, audioid);
                }
            };
            LogUtil.d(TAG, "setFav  : " + item2BeChanged.getId());

            if (item2BeChanged.getFav() == Constants.UNFAV) {
                AudioCenterHttpManager.getInstance(mContext).addFavorite(mSetFavSubscriber, item2BeChanged.getId(), item2BeChanged.getCategoryType(), item2BeChanged.getGroup());
            } else {
                AudioCenterHttpManager.getInstance(mContext).removeFavorite(mSetFavSubscriber, item2BeChanged.getId(), item2BeChanged.getCategoryType(), item2BeChanged.getGroup());

            }
        } else {

        }

    }

    void onUpdate2ServerComplete(int resultCode, long audioid) {

    }

    /**
     * @param audioId audioId
     * @return if contains ,replace the item and return the index,
     * else return -1
     */
    private int isCurrentPlaylistContain(long audioId) {
        for (int i = 0; i < wholePlayList.size(); i++) {
            PlayListItem item = wholePlayList.get(i);
            if (item.getId().longValue() == audioId) {
                return i;
            }
        }
        return -1;
    }

    class PlayListManagerException extends Exception {
        public PlayListManagerException(String msg) {
            super(msg);
        }

    }

    abstract void loadMore();

    abstract void updateLocalPlayList();


}
