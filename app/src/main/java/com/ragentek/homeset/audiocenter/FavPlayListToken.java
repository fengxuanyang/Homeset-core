package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
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

public class FavPlayListToken extends BasePlayListToken {
    private static final String TAG = "FavPlayListManager";

    public FavPlayListToken(TagDetail tag, Context context) {
        super(tag, context);
    }

    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGFav();
        super.init(playListManagerListener);
    }

    @Override
    void loadMore(IPlayItemUpdateListener listener) {

    }

    @Override
    void updateLocalPlayList(IPlayListLoadListener listener, long id) {

    }


//    @Override
//    public void loadMore() {
//        if (isInitted) {
//            getTAGFav();
//            return;
//        }
//        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
//    }
//
//    @Override
//    void updateLocalPlayList() {
//
//    }


    private void getTAGFav() {
        LogUtil.d(TAG, "getTAGFav: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<FavoriteResultVO> mloadDataSubscriber = new Subscriber<FavoriteResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "getTAGFav onCompleted: ");
                currentPage++;
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "getTAGFav onError: " + e.getMessage());
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
                        playListItems.add(AudioCenterUtils.decoratorFavoriteVO(tagResult.getFavorites().get(i)));
                    }
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mContext).getFavorites(mloadDataSubscriber, currentPage, PAGE_COUNT);
    }
}
