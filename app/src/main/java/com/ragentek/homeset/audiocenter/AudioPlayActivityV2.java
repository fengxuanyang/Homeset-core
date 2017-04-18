package com.ragentek.homeset.audiocenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
//import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.service.MyMediaPlayerControl;
import com.ragentek.homeset.audiocenter.utils.AudioCenterUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.fragment.AlbumFragment;
import com.ragentek.homeset.audiocenter.view.fragment.MusicFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayBaseFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayListFragment;
import com.ragentek.homeset.audiocenter.view.fragment.PlayStateFragment;
import com.ragentek.homeset.audiocenter.view.fragment.RadioFragment;
import com.ragentek.homeset.audiocenter.view.fragment.SingleMusicFragment;
import com.ragentek.homeset.audiocenter.view.widget.ImageWithText;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.task.event.BackHomeEvent;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;


public class AudioPlayActivityV2 extends AudioCenterBaseActivity implements MyMediaPlayerControl, PlayListFragment.PlayListListener {


    private AudioPlayerHandler mhandler = new AudioPlayerHandler();
    private PlayBaseFragment mCurrentPlayFragment;
    //    private PlayListDetail mCurrentAudioPlayListDetail;
    private PlayListFragment playListFragment;
    private static final String STATEFRAGMENTTAG = "playstatefragment";

    private int currentPlayListType = Constants.PLAYLIST_ALBUM;
    private PlayListItem mcurrentAudio;
    private final String PLAYLIST = "playlist";
    //for media player
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerHandler;

    private BasePlayListToken mPlayListManager;

    private PlayListListener mPlayListListener = new PlayListListener();
    //    private MyMediaListener mediaListener;
    private boolean needUpdatePlayProgress = true;
    private final String PERCENTAGE = "percentage";
    private TagDetail mTagDetail;
    private String eventType;
    private int currentPlayIndex = 0;

    @BindView(R.id.image_playorpause)
    ImageWithText playorpause;
    @BindView(R.id.image_play_next)
    ImageWithText playNext;
    @BindView(R.id.image_play_pre)
    ImageWithText playPre;
    @BindView(R.id.image_play_list)
    ImageWithText playList;

    @BindView(R.id.image_fav)
    ImageWithText favIV;
    @BindView(R.id.image_play_mode)
    ImageWithText playMode;

    @BindView(R.id.iv_back)
    ImageView backIV;
    @BindView(R.id.audio_name)
    TextView audioName;
    @BindView(R.id.play_seek)
    SeekBar playSeeBar;
    @BindView(R.id.tv_play_currenttime)
    TextView currenttime;
    @BindView(R.id.tv_play_totaltime)
    TextView totaltime;
    @BindView(R.id.top_progressbar)
    ProgressBar loadProgress;
    @BindView(R.id.bottom_bar)
    View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.audioenter_activity_play);
        ButterKnife.bind(this);
        updateView();
        updateAudioData();
        EventBus.getDefault().register(this);
        mediaPlayerHandler = MediaPlayerManager.getInstance(this).geMediaPlayerHandler();
        mediaPlayerHandler.addMediaPlayerListener(mMediaPlayerListener);
    }


    private void updateView() {
        updatePlayControl(false);
        playSeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                needUpdatePlayProgress = true;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_SEEKBAR_CHANGED;
                Bundle bundle = new Bundle();
                bundle.putFloat(PERCENTAGE, seekBar.getProgress() / (float) seekBar.getMax());
                msg.setData(bundle);
                mhandler.sendMessage(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                needUpdatePlayProgress = false;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }

    private void updateAudioData() {
        LogUtil.d(TAG, "updateAudioData ::" + mTagDetail);
        //TAG contains music radio etc
        if (mTagDetail == null) {
            TagDetail tag = getTagDetail();
            LogUtil.d(TAG, "onCreate getName:" + tag.getName());
            LogUtil.d(TAG, "onCreate getCategoryID:" + tag.getCategoryID());
            if (tag != null) {
                if (tag.getName() == null) {
                    for (int i = 0; i < Constants.CATEGORYTAG.values().length; i++) {
                        if (Constants.CATEGORYTAG.values()[i].getType() == tag.getCategoryID()) {
                            tag.setName(Constants.CATEGORYTAG.values()[i].getName());
                            break;
                        }
                    }
                }
                mTagDetail = tag;
            }
        }
        LogUtil.d(TAG, "updateAudioData ::" + mTagDetail.getName());

        switch (mTagDetail.getCategoryID()) {
            case Category.ID.CROSS_TALK:
            case Category.ID.CHINA_ART:
            case Category.ID.HEALTH:
            case Category.ID.STORYTELLING:
            case Category.ID.STOCK:
            case Category.ID.HISTORY:
                currentPlayListType = Constants.PLAYLIST_ALBUM;
                mPlayListManager = new AlbumPlayListToken(mTagDetail, this);
                break;
            case Category.ID.RADIO:
                mPlayListManager = new RadioPlayListToken(mTagDetail, this);
                currentPlayListType = Constants.PLAYLIST_RADIO;
                break;
            case Category.ID.MUSIC:
                currentPlayListType = Constants.PLAYLIST_MUSIC;
                mPlayListManager = new MusicPlayListToken(mTagDetail, this);
                break;
            case Constants.CATEGORY_FAV:
                currentPlayListType = Constants.PLAYLIST_MUSIC;
                currentPlayListType = Constants.PLAYLIST_FAV;
                mPlayListManager = new FavPlayListToken(mTagDetail, this);
                break;
        }
        audioName.setText(mTagDetail.getName());
        mPlayListManager.init(mPlayListListener);
    }

    private TagDetail getTagDetail() {
        TagDetail currentTag = null;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            eventType = bundle.getString(Constants.TASKEVENT_TYPE);
            currentTag = (TagDetail) bundle.getSerializable(Constants.CATEGORY_TAG);
        }
        return currentTag;
    }


    @Override
    public void onBackPressed() {
        //fav mode
        if (currentPlayListType == Constants.PLAYLIST_FAV) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }


    //TODO for test
    @OnClick(R.id.image_play_pre)
    void playPre() {
        LogUtil.d(TAG, "playPre:");
        int id = currentPlayIndex;
        if (mPlayListManager.gePlayList() != null) {

            if (id > 0) {
                id--;
            }
            setCurrentAudio(id);
            updateWholeView();
        }
    }


    @OnClick(R.id.image_play_next)
    void playNext() {
        LogUtil.d(TAG, "playNext:");
        int id = currentPlayIndex;

        if (mPlayListManager.gePlayList() != null) {
            if (id < mPlayListManager.gePlayList().size() - 1) {
                id++;
            } else {
                id = 0;
            }
            setCurrentAudio(id);
            updateWholeView();
        }


    }

    //TODO for test switch   the fragment
    @OnClick(R.id.image_play_mode)
    void switchPlayMode() {
        LogUtil.d(TAG, "switchPlayMode:");
        if (mcurrentAudio.getAudioType() == Constants.AUDIO_TYPE_ALBUM) {
            RadioVO radio = new RadioVO();
            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_RADIO, 0, 0L);
            item.setAudio(radio);
            mcurrentAudio = item;
            switchPlayFragment(getAndUpdateAudioPlayFragment(mcurrentAudio), Constants.AUDIO_TYPE_MUSIC + "");
            updatePlayControlFavUI();
        } else {
//            updateAudioType();
        }

    }


    @OnClick(R.id.image_play_list)
    void showPlayList() {
        LogUtil.d(TAG, "showPlayList");
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PLAYLIST);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            playListFragment = (PlayListFragment) fragment;//= new PlayListFragment(mAlbums);

            ft.attach(playListFragment).commit();
        } else {
            playListFragment = new PlayListFragment();

            ft.add(playListFragment, PLAYLIST).commit();
        }
        playListFragment.addData(mPlayListManager.gePlayList());

    }

    @OnClick(R.id.iv_back)
    void doBack() {
        LogUtil.d(TAG, "doBack eventType:" + eventType + ",currentPlayListType:" + currentPlayListType);
        //fav mode and speech command ,back to launcher
        if (currentPlayListType == Constants.PLAYLIST_FAV || Constants.TASKEVENT_TYPE_SPEECH.equals(eventType)) {
            EventBus.getDefault().post(new BackHomeEvent());
        }
        finish();
    }


    @OnClick(R.id.image_fav)
    void setFav() {
        LogUtil.d(TAG, "setFav ");

        mPlayListManager.updateFav2Server(mPlayListManager.getPlayListItemFromIndext(currentPlayIndex).getId().longValue());

    }


    /**
     * @param position position
     */
    private void setCurrentAudio(int position) {
        LogUtil.d(TAG, "setCurrentAudio:" + position);
        currentPlayIndex = position;
        mcurrentAudio = mPlayListManager.getPlayListItemFromIndext(position);
    }


    private void updatePlayControlFavUI() {
        LogUtil.d(TAG, "updateFavUI:" + mcurrentAudio.getFav());
        if (mcurrentAudio.getFav() == Constants.FAV) {
            favIV.setImageResource(R.drawable.control_fav);
        } else {
            favIV.setImageResource(R.drawable.control_unfav);
        }
    }

    @OnClick(R.id.image_playorpause)
    void playorpause() {
        LogUtil.d(TAG, "playorpause:");
        Message msg = new Message();
        msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_PAUSE;
        mhandler.sendMessage(msg);
    }


    public void switchPlayFragment(PlayBaseFragment to, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentPlayFragment == null) {
            transaction.replace(R.id.fragment_container, to, tag).commit();
        } else {
            if (mCurrentPlayFragment != to) {
                if (!to.isAdded()) {
                    transaction.replace(R.id.fragment_container, to, tag + "").commit();
                } else {
                    transaction.remove(mCurrentPlayFragment).show(to).commit();
                }
            }
        }
        mCurrentPlayFragment = to;
    }


    /**
     * TODO is the tag and album same  ?
     * use the getAudioType  as  the  fragment tag
     *
     * @param data PlayListItem
     * @return PlayBaseFragment
     */
    private PlayBaseFragment getAndUpdateAudioPlayFragment(PlayListItem data) {
        LogUtil.d(TAG, "getAndUpdateAudioPlayFragment:" + data.getAudioType());
        PlayBaseFragment fragment = null;
        Fragment view = getSupportFragmentManager().findFragmentByTag(data.getAudioType() + "");
        loadProgress.setVisibility(View.GONE);
        switch (data.getAudioType()) {
            case Constants.AUDIO_TYPE_ALBUM:
                if (view == null) {
                    fragment = new AlbumFragment();
                } else {
                    fragment = (AlbumFragment) view;
                }
                break;
            case Constants.AUDIO_TYPE_MUSIC:
                if (view == null) {
                    fragment = new MusicFragment();
                } else {
                    fragment = (MusicFragment) view;
                }
                break;
            case Constants.AUDIO_TYPE_RADIO:
                if (view == null) {
                    fragment = new RadioFragment();
                } else {
                    fragment = (RadioFragment) view;
                }
                break;
            case Constants.AUDIO_TYPE_SINGLE_MUSIC:
                if (view == null) {
                    fragment = new SingleMusicFragment();
                } else {
                    fragment = (SingleMusicFragment) view;
                }
                break;
        }

        //update the data
        fragment.setPlaydata(data.getAudio());
        return fragment;
    }


    private PlayBaseFragment getStateFragment(PlayStateFragment.PLAYSTATE state) {
        PlayBaseFragment playStateFragment = null;
        Fragment view = getSupportFragmentManager().findFragmentByTag(STATEFRAGMENTTAG);
        loadProgress.setVisibility(View.GONE);
        if (view == null) {
            playStateFragment = new PlayStateFragment();
        } else {
            playStateFragment = (PlayStateFragment) view;
        }
        playStateFragment.setPlaydata(state);
        return playStateFragment;
    }


    private void updatePlayFragment() {
        LogUtil.d(TAG, "updatePlayFragment:" + currentPlayListType);
        switch (currentPlayListType) {
            //FAV mode ,switch the fragment
            case Constants.PLAYLIST_FAV:
                switchPlayFragment(getAndUpdateAudioPlayFragment(mcurrentAudio), mcurrentAudio.getAudioType() + "");
                break;
            case Constants.PLAYLIST_MUSIC:
                MusicVO music = (MusicVO) mcurrentAudio.getAudio();
                mCurrentPlayFragment.setPlaydata(music);

                break;
            case Constants.PLAYLIST_RADIO:
                RadioVO radio = (RadioVO) mcurrentAudio.getAudio();
                mCurrentPlayFragment.setPlaydata(radio);
                break;
            case Constants.PLAYLIST_ALBUM:
                AlbumVO album = (AlbumVO) mcurrentAudio.getAudio();
                mCurrentPlayFragment.setPlaydata(album);
                break;
        }
    }

    private void updatePlayListFragmert(int position) {
        if (playListFragment != null) {
            playListFragment.setCurrentPlayIndext(position);
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mediaPlayerHandler != null) {
            mediaPlayerHandler.clearPlayList();
        }
        mhandler = null;
    }


    //for play list
    @Override
    public void onItemClick(int position) {
        LogUtil.d(TAG, "onItemClick: " + position + ",currentPlayListType:" + currentPlayListType);
        setCurrentAudio(position);
        updateWholeView();
    }


    @Override
    public void onCloseClick() {
        detachFragment(PLAYLIST);
    }

    @Override
    public void onFavClick(int position) {

    }

    @Override
    public void onLoadMore() {
        mPlayListManager.loadMore();
    }


    private void detachFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        LogUtil.d(TAG, "showPlayList ::" + fragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.detach(fragment).commit();
        }
    }

    @Override
    public void play(int position) {
        LogUtil.d(TAG, "play  position:" + position);
        Message msg = new Message();
        msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_SELECTED;
        mhandler.position = position;
        mhandler.sendMessage(msg);
    }

    @Override
    public void setPlayList(final List<PlayItem> list, final int position) {
        LogUtil.d(TAG, "setPlayList ::" + list.size());
        for (int i = 0; i < list.size(); i++) {
            PlayItem item = list.get(i);
            LogUtil.d(TAG, "setPlayList ::" + item.getTitle() + "" + item.getPlayUrl());

        }
        Message msg = new Message();
        msg.what = AudioPlayerHandler.MSG_MEDIA_PLAYLIST_COMPLETE;
        mhandler.currentPlayList = list;
        mhandler.position = position;
        mhandler.sendMessage(msg);
    }


    @Override
    public void addPlayList(List<PlayItem> list, int position) {
        LogUtil.d(TAG, "addPlayList:" + list.size());
        mediaPlayerHandler.addPlayList(list);
    }

    @Subscribe
    public void onAudioFavEvent(final PushAudioFavEvent fav) {


    }


    private void updateWholeView() {
        updatePlayControlFavUI();
        updatePlayFragment();
        updatePlayListFragmert(currentPlayIndex);
    }

    //TODO div music and radio
    private void updatePlayControl(boolean clickAble) {
        playNext.setClickable(clickAble);
        playorpause.setClickable(clickAble);
        playPre.setClickable(clickAble);
        favIV.setClickable(clickAble);
        playMode.setClickable(clickAble);
    }

    private IMediaPlayerListener mMediaPlayerListener = new IMediaPlayerListener.Stub() {
        @Override
        public IBinder asBinder() {
            return super.asBinder();
        }

        @Override
        public void initComplete() throws RemoteException {
            LogUtil.d(TAG, "initComplete ::");
            if (mhandler != null) {
                mhandler.isMediaInitComplete = true;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_INIT_COMPLETE;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onSoundPrepared() throws RemoteException {
            LogUtil.d(TAG, "onSoundPrepared ::");
        }

        @Override
        public void onPlayStart() throws RemoteException {
            LogUtil.i(TAG, "onPlayStart");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_START;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onPlayProgress(int currPos, int duration) throws RemoteException {
            LogUtil.d(TAG, "onPlayProgress :currPos:" + currPos + ",duration:" + duration);
            if (mhandler != null) {
                mhandler.currPos = currPos;
                mhandler.duration = duration;
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_ONPROGRESS;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onPlayStop() throws RemoteException {
            LogUtil.i(TAG, "onPlayStop");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_PLAY_STOP;
                mhandler.sendMessage(msg);
            }
        }

        @Override
        public void onSoundPlayComplete() throws RemoteException {
            LogUtil.i(TAG, "onSoundPlayComplete");
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = AudioPlayerHandler.MSG_MEDIA_SOUME_PREPARED;
                mhandler.sendMessage(msg);
            }
        }
    };


    private class PlayListListener implements PlayListManagerListener {

        @Override
        public void initComplete(int resultcode, List<PlayListItem> resultmessage) {
            switch (resultcode) {
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_SUCCESS:
                    switchPlayFragment(getAndUpdateAudioPlayFragment(resultmessage.get(currentPlayIndex)), resultmessage.get(currentPlayIndex).getAudioType() + "");
                    break;
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_NONE:
                    switchPlayFragment(getStateFragment(PlayStateFragment.PLAYSTATE.DATANULL), STATEFRAGMENTTAG);

                    break;
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_ERROR_NET:
                    switchPlayFragment(getStateFragment(PlayStateFragment.PLAYSTATE.NETERROR), STATEFRAGMENTTAG);
                    break;
            }
        }

        @Override
        public void loadMoreComplete(int resultcode, List<PlayListItem> resultmessage) {
            switch (resultcode) {
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_SUCCESS:
                    if (playListFragment != null) {
                        playListFragment.addData(resultmessage);
                    }
                    break;
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_NONE:
                    LogUtil.e(TAG, "PLAYLISTMANAGER_RESULT_NONE : ");
                    Toast.makeText(AudioPlayActivityV2.this, "PLAYLISTMANAGER_RESULT_NONE", Toast.LENGTH_SHORT);

                    break;
                case BasePlayListToken.PLAYLISTMANAGER_RESULT_ERROR_NET:
                    Toast.makeText(AudioPlayActivityV2.this, "PLAYLISTMANAGER_RESULT_ERROR_NET", Toast.LENGTH_SHORT);
                    break;
            }


        }

        @Override
        public void onUpdate2ServerComplete(int resultcode, long id) {

        }
    }


    private class AudioPlayerHandler extends Handler {
        private static final int MSG_MEDIA_INIT_COMPLETE = 0;
        private static final int MSG_MEDIA_PLAYLIST_COMPLETE = MSG_MEDIA_INIT_COMPLETE + 1;
        private static final int MSG_MEDIA_PLAY_SELECTED = MSG_MEDIA_INIT_COMPLETE + 2;
        private static final int MSG_MEDIA_PLAY_PAUSE = MSG_MEDIA_INIT_COMPLETE + 3;
        private static final int MSG_MEDIA_PLAY_ONPROGRESS = MSG_MEDIA_INIT_COMPLETE + 4;
        private static final int MSG_MEDIA_PLAY_START = MSG_MEDIA_INIT_COMPLETE + 5;
        private static final int MSG_MEDIA_PLAY_STOP = MSG_MEDIA_INIT_COMPLETE + 6;
        private static final int MSG_MEDIA_SEEKBAR_CHANGED = MSG_MEDIA_INIT_COMPLETE + 7;
        private static final int MSG_MEDIA_UPDATE_PLAYLIST = MSG_MEDIA_INIT_COMPLETE + 8;
        private static final int MSG_MEDIA_SOUME_PREPARED = MSG_MEDIA_INIT_COMPLETE + 9;
        boolean isMediaInitComplete;
        List<PlayItem> currentPlayList;
        int position;
        int duration;
        int currPos;

        @Override
        public void handleMessage(Message msg) {
            LogUtil.d(TAG, "MSG:" + msg.what + ",isMediaInitComplete:" + isMediaInitComplete);
            switch (msg.what) {
                case MSG_MEDIA_INIT_COMPLETE:
                    if (currentPlayList != null) {
                        mediaPlayerHandler.setPlayList(currentPlayList, msg.arg1);
                    }
                    break;
                case MSG_MEDIA_PLAYLIST_COMPLETE:
                    LogUtil.d(TAG, "MSG:" + msg.what + ",currentPlayList:" + currentPlayList + ",size:" + currentPlayList.size());

                    if (isMediaInitComplete && currentPlayList != null && currentPlayList.size() > 0) {
                        mediaPlayerHandler.setPlayList(currentPlayList, position);
                    }
                    break;
                case MSG_MEDIA_PLAY_SELECTED:
                    if (isMediaInitComplete) {
                        mediaPlayerHandler.play(position);
                    }
                    break;
                case MSG_MEDIA_PLAY_PAUSE:
                    if (isMediaInitComplete) {
                        mediaPlayerHandler.playOrPause();
                    }
                    break;
                case MSG_MEDIA_PLAY_ONPROGRESS:
                    currenttime.setText(AudioCenterUtils.formatTime(currPos));
                    totaltime.setText(AudioCenterUtils.formatTime(duration));
                    if (needUpdatePlayProgress && duration != 0) {
                        playSeeBar.setProgress((int) (100 * currPos / (float) duration));
                    }
                    break;
                case MSG_MEDIA_PLAY_STOP:
                    playorpause.setImageResource(R.drawable.player_play);
                    break;
                case MSG_MEDIA_PLAY_START:
                    updatePlayControl(true);
                    playorpause.setImageResource(R.drawable.player_pause);
                    break;
                case MSG_MEDIA_SEEKBAR_CHANGED:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        float percentage = bundle.getFloat(PERCENTAGE);
                        LogUtil.d(TAG, "percentage:" + percentage);
                        mediaPlayerHandler.seekToByPercent(percentage);
                    }
                    break;
                case MSG_MEDIA_SOUME_PREPARED:
                    playNext();
                    break;
            }
        }
    }
}
