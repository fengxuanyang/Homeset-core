package com.ragentek.homeset.audiocenter;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.FavoriteVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.constants.CategoryEnum;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class FavPlayListManager extends BasePlayListManager {
    private static final String TAG = "FavPlayListManager";

    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGMusic();
        super.init(playListManagerListener);
    }

    @Override
    public void loadMore() {
        if (isInitted) {
            getTAGFav();
            return;
        }
        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
    }

    @Override
    void update2Server(PlayListItem item) {
        LogUtil.d(TAG, "update2Server  ID: " + item.getId());
    }

    private void getTAGFav() {
        LogUtil.d(TAG, "getTAGMusics: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<FavoriteResultVO> mloadDataSubscriber = new Subscriber<FavoriteResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "getTAGMusics onCompleted: ");
                currentPage++;
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "getTAGMusics onError: " + e.getMessage());

            }

            @Override
            public void onNext(FavoriteResultVO tagResult) {
                if (tagResult == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getFavorites() == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();

                    int totalSize = tagResult.getFavorites().size();
                    LogUtil.d(TAG, "totalSize: " + totalSize);

                    // new add fav is on the top
                    for (int i = totalSize - 1; i > -1; i--) {
                        playListItems.add(decoratorFavoriteVO(tagResult.getFavorites().get(i)));
                    }
                }
            }
        };
        AudioCenterHttpManager.getInstance(mContext).getFavorites(mloadDataSubscriber, currentPage, PAGE_COUNT);

    }


    private void requestPlayDataComplete(int resultCode, List<PlayListItem> resultmessage) {
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
}
