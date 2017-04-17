package com.ragentek.homeset.audiocenter;

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

public class AlbumPlayListManager extends BasePlayListManager {
    private static final String TAG = "AlbumPlayListManager";

    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGAlbums();
        super.init(playListManagerListener);
    }

    @Override
    public void loadMore() {
        if (isInitted) {
            getTAGAlbums();
            return;
        }
        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
    }

    @Override
    void update2Server(PlayListItem item) {
        LogUtil.d(TAG, "update2Server  ID: " + item.getId());
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
