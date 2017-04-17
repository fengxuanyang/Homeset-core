package com.ragentek.homeset.audiocenter.service;

import android.content.Context;
import android.util.Log;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * manager the playlist
 * Created by xuanyang.feng on 2017/4/13.
 */

public class PlayListManager {
    private static final String TAG = "PlayListManager";
    private static PlayListManager mPlayListManager;
    private List<PlayListItem> wholePlayList;
    private WeakReference<Context> mContext;
    private int currentPlayListType = Constants.PLAYLIST_ALBUM;


    private TagDetail currenTag;

    public static final int PLAYLISTMANAGER_RESULT_ERROR_NET = -1;
    public static final int PLAYLISTMANAGER_RESULT_ERROR_NONTINIT = -2;

    public static final int PLAYLISTMANAGER_RESULT_SUCCESS = 0;
    public static final int PLAYLISTMANAGER_RESULT_NONE = 1;
    private boolean isInitted = false;
    private PlayListManagerListener mPlayListManagerListener;
    private int currentPage;


    private PlayListManager(Context context) {
        mContext = new WeakReference<Context>(context.getApplicationContext());
    }

    public static PlayListManager getInstance(Context context) {
        if (mPlayListManager == null) {
            synchronized (PlayListManager.class) {
                mPlayListManager = new PlayListManager(context);
            }

        }
        return mPlayListManager;
    }

    public void initPlayList(TagDetail tagDetail, PlayListManagerListener anagerListener) {
        isInitted = false;
        mPlayListManagerListener = anagerListener;
    }


    public void loadMore() {
        if (!isInitted) {
            Log.e(TAG, "loadMore: error PlayListManager not init");
            return;
        } else {
            updateAudioData(currenTag);
        }
    }


    //TODO now

    /**
     * @param item
     */
    public void updateItemFav(PlayListItem item) {
        if (!isInitted) {
            Log.e(TAG, "loadMore: error PlayListManager not init");
            return;
        } else {
            PlayListItem listItem = getIndexFromID(item.getId().longValue());
            if (listItem != null) {
                listItem.updateFav();
            }


        }
    }

    private PlayListItem getIndexFromID(long id) {
        for (PlayListItem item : wholePlayList) {
            if (item.getId().longValue() == id) {
                return item;
            }
        }
        return null;
    }

    public TagDetail getCurrenTag() {
        return currenTag;
    }


    private boolean isInitialize() {
        return isInitted;
    }


    private void updateAudioData(TagDetail tagDetail) {
        LogUtil.d(TAG, "updateAudioData ::" + tagDetail);
        //TAG contains music radio etc
        LogUtil.d(TAG, "updateAudioData ::" + tagDetail.getName());
        switch (tagDetail.getCategoryID()) {
            case Category.ID.CROSS_TALK:
            case Category.ID.CHINA_ART:
            case Category.ID.HEALTH:
            case Category.ID.STORYTELLING:
            case Category.ID.STOCK:
            case Category.ID.HISTORY:
                currentPlayListType = Constants.PLAYLIST_ALBUM;
                getTAGAlbums(tagDetail);
                break;
            case Category.ID.RADIO:
                currentPlayListType = Constants.PLAYLIST_RADIO;
//                getTAGRadio(tagDetail);
                break;
            case Category.ID.MUSIC:
                currentPlayListType = Constants.PLAYLIST_MUSIC;
//                getTAGMusics(tagDetail);
                break;
            case Constants.CATEGORY_FAV:
                currentPlayListType = Constants.PLAYLIST_FAV;
//                getFav(tagDetail);
                break;
        }
    }

    private void getTAGAlbums(final TagDetail currentTag) {
        LogUtil.d(TAG, "getAlbums: " + currentTag.getCategoryID() + ":getName" + currentTag.getName());
        Subscriber<AlbumResultVO> mloadDataSubscriber = new Subscriber<AlbumResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
                isInitted = true;
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
                        PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_ALBUM, currentTag.getCategoryID(), album.getId());
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
        AudioCenterHttpManager.getInstance(mContext.get()).getAlbums(mloadDataSubscriber, currentTag.getCategoryID(), currentTag.getName() == null ? Constants.DEFULT_CROSS_TALK : currentTag.getName(), currentPage, PAGE_COUNT);
    }


    private void updateFav(int index) {
        LogUtil.d(TAG, "setFav ");
        final PlayListItem playitem = wholePlayList.get(index);
        Subscriber<String> mSetFavSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onNext onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onNext result: " + e.getMessage());
            }

            @Override
            public void onNext(String result) {
                LogUtil.d(TAG, "onNext result: " + result);
                playitem.updateFav();
            }
        };
        LogUtil.d(TAG, "setFav  : " + playitem.getId());

        if (playitem.getFav() == Constants.UNFAV) {
            AudioCenterHttpManager.getInstance(mContext.get()).addFavorite(mSetFavSubscriber, playitem.getId(), playitem.getCategoryType(), playitem.getGroup());
        } else {
            AudioCenterHttpManager.getInstance(mContext.get()).removeFavorite(mSetFavSubscriber, playitem.getId(), playitem.getCategoryType(), playitem.getGroup());

        }
    }

    private void requestPlayDataComplete(int resultCode, List<PlayListItem> resultmessage) {
        if (!isInitted) {
            mPlayListManagerListener.initComplete(resultCode, resultmessage);
        } else {
            mPlayListManagerListener.loadMoreComplete(resultCode, resultmessage);
        }
    }

    public interface PlayListManagerListener {
        void initComplete(int resultcode, List<PlayListItem> resultmessage);

        void loadMoreComplete(int resultcode, List<PlayListItem> resultmessage);

    }
}
