package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class AlbumPlayListToken extends BasePlayListToken {
    private static final String TAG = "AlbumPlayListToken";

    public AlbumPlayListToken(TagDetail tag, Context context) {
        super(tag, context);
    }


    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGAlbums();
        super.init(playListManagerListener);
    }

    @Override
    void loadMore(IPlayItemUpdateListener listener) {

    }

    @Override
    void updateLocalPlayList(IPlayListLoadListener listener, long id) {

    }


    private void getTAGAlbums() {
        LogUtil.d(TAG, "getAlbums: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<AlbumResultVO> mloadDataSubscriber = new Subscriber<AlbumResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(AlbumResultVO tagResult) {
                if (tagResult == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getAlbums() == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    //for audio playlist  start
                    List<PlayListItem> playListItems = new ArrayList<>();
                    for (AlbumVO album : tagResult.getAlbums()) {
                        PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_ALBUM, mTagDetail.getCategoryID(), album.getId());
                        LogUtil.d(TAG, "fav:" + album.getFavorite());
                        LogUtil.d(TAG, album.getTitle());
                        item.setFav(album.getFavorite());
                        item.setGroup(Constants.GROUP_ALBUM);
                        item.setAudio(album);
                        playListItems.add(item);
                    }
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mContext).getAlbums(mloadDataSubscriber, mTagDetail.getCategoryID(), mTagDetail.getName() == null ? Constants.DEFULT_CROSS_TALK : mTagDetail.getName(), currentPage, PAGE_COUNT);
    }
}
