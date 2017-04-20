package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;
import com.ragentek.protocol.messages.http.audio.RadioResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class RadioPlayListToken extends BasePlayListToken {
    private static final String TAG = "RadioPlayListToken";

    public RadioPlayListToken(TagDetail tag, Context context) {
        super(tag, context);
    }

    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGRadio();
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
//            getTAGRadio();
//            return;
//        }
//        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
//    }
//
//    @Override
//    void updateLocalPlayList() {
//
//    }


    private void getTAGRadio() {
        LogUtil.d(TAG, "getTAGFav: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<RadioResultVO> mloadDataSubscriber = new Subscriber<RadioResultVO>() {
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
            public void onNext(RadioResultVO tagResult) {
                if (tagResult == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getRadios() == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();
                    for (RadioVO radio : tagResult.getRadios()) {
                        PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_RADIO, mTagDetail.getCategoryID(), radio.getId());
                        LogUtil.d(TAG, "fav:" + radio.getFavorite());
                        item.setFav(radio.getFavorite());
                        item.setGroup(Constants.GROUP_RADIO);
                        item.setAudio(radio);
                        playListItems.add(item);
                    }
                    int totalSize = tagResult.getRadios().size();
                    LogUtil.d(TAG, "totalSize: " + totalSize);
                    // new add fav is on the top

                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                }
            }
        };
        AudioCenterHttpManager.getInstance(mContext).getRadiosByTAG(mloadDataSubscriber, mTagDetail.getRadioType(), mTagDetail.getProvince(), currentPage, PAGE_COUNT);

    }


}
