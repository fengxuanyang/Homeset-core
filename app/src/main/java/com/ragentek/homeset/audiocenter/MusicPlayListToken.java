package com.ragentek.homeset.audiocenter;

import android.content.Context;

import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment.PAGE_COUNT;

/**
 * Created by xuanyang.feng on 2017/4/17.
 */

public class MusicPlayListToken extends BasePlayListToken {
    private static final String TAG = "MusicPlayListToken";

    public MusicPlayListToken(TagDetail tag, Context context) {
        super(tag, context);
    }

    @Override
    public void init(PlayListManagerListener playListManagerListener) {
        getTAGMusic();
        super.init(playListManagerListener);
    }

    @Override
    public void loadMore() {
        if (isInitted) {
            getTAGMusic();
            return;
        }
        LogUtil.e(TAG, "loadMore error not init isInitted: " + isInitted);
    }

    @Override
    void update2Server(PlayListItem item) {
        LogUtil.d(TAG, "update2Server  ID: " + item.getId());
    }

    private void getTAGMusic() {
        LogUtil.d(TAG, "getTAGMusics: " + mTagDetail.getCategoryID() + ":getName" + mTagDetail.getName());
        Subscriber<MusicResultVO> mloadDataSubscriber = new Subscriber<MusicResultVO>() {
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
            public void onNext(MusicResultVO tagResult) {
                if (tagResult == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_ERROR_NET, null);
                } else if (tagResult.getMusics() == null) {
                    requestPlayDataComplete(PLAYLISTMANAGER_RESULT_NONE, null);
                } else {
                    currentPage++;
                    //for audio playlist  start
                    List<PlayListItem> playListItems = new ArrayList<PlayListItem>();
                    //filterred is used for musicplayfragment
                    List<MusicVO> filterred = new ArrayList<>();
                    for (int i = 0; i < tagResult.getMusics().size(); i++) {
                        MusicVO music = tagResult.getMusics().get(i);
                        if (music != null && music.getSong_name() != null) {
                            LogUtil.d(TAG, "getTAGMusics :" + music.getSong_name());
                            LogUtil.d(TAG, "getCover_url :" + music.getCover_url());
                            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_MUSIC, mTagDetail.getCategoryID(), music.getId());
                            item.setAudio(music);
                            item.setFav(music.getFavorite());
                            item.setGroup(Constants.GROUP_MUSIC);
                            playListItems.add(item);
                            filterred.add(music);
                            LogUtil.d(TAG, "setPlayList :i:" + i + "" + music.getSong_name() + "" + music.getPlay_url());
                        }
                        requestPlayDataComplete(PLAYLISTMANAGER_RESULT_SUCCESS, playListItems);
                    }

                }
            }
        };

        AudioCenterHttpManager.getInstance(mContext).getMusics(mloadDataSubscriber, mTagDetail.getName(), currentPage, PAGE_COUNT);
    }


}
