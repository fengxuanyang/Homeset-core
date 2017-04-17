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

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.service.MyMediaPlayerControl;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.utils.Utils;
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
import com.ragentek.protocol.commons.audio.FavoriteVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.constants.CategoryEnum;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;
import com.ragentek.protocol.messages.http.audio.RadioResultVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class AudioPlayActivityV2 extends AudioCenterBaseActivity implements MyMediaPlayerControl, PlayListFragment.PlayListListener {


    private AudioPlayerHandler mhandler = new AudioPlayerHandler();
    private PlayBaseFragment mCurrentPlayFragment;
    private PlayListDetail mCurrentAudioPlayListDetail;
    private PlayListFragment playListFragment;
    private static final String STATEFRAGMENTTAG = "playstatefragment";
    private int currentPage = 1;
    private final int PAGE_COUNT = 20;
    private final int ADD_FAV_INDEX = 0;

    private int currentPlayListType = Constants.PLAYLIST_ALBUM;
    private PlayListItem mcurrentAudio;
    private final String PLAYLIST = "playlist";
    //for media player
    private MediaPlayerManager.MediaPlayerHandler mediaPlayerHandler;


    //    private MyMediaListener mediaListener;
    private boolean needUpdatePlayProgress = true;
    private final String PERCENTAGE = "percentage";
    private TagDetail mTagDetail;
    private String eventType;

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
            //TODO is the same ui ?
            case Category.ID.CROSS_TALK:
            case Category.ID.CHINA_ART:
            case Category.ID.HEALTH:
            case Category.ID.STORYTELLING:
            case Category.ID.STOCK:
            case Category.ID.HISTORY:
                currentPlayListType = Constants.PLAYLIST_ALBUM;
                getTAGAlbums(mTagDetail);
                break;
            case Category.ID.RADIO:
                getTAGRadio(mTagDetail);
                currentPlayListType = Constants.PLAYLIST_RADIO;
                break;
            case Category.ID.MUSIC:
                currentPlayListType = Constants.PLAYLIST_MUSIC;
                getTAGMusics(mTagDetail);
                break;
            case Constants.CATEGORY_FAV:
                currentPlayListType = Constants.PLAYLIST_MUSIC;
                currentPlayListType = Constants.PLAYLIST_FAV;
                getFav();
                break;
        }
        audioName.setText(mTagDetail.getName());
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

    //TODO
    private void getTAGRadio(final TagDetail currentTag) {
        LogUtil.d(TAG, "getTAGRadio: ");
        LogUtil.d(TAG, "getTAGRadio: " + currentTag.getCategoryID() + ":getName" + currentTag.getName());
        Subscriber<RadioResultVO> mloadDataSubscriber = new Subscriber<RadioResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(RadioResultVO tagResult) {
                if (tagResult != null) {
                    //for audio playlist  start
                    List<PlayListItem> playListItems = new ArrayList<>();

                    for (RadioVO radio : tagResult.getRadios()) {
                        PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_RADIO, currentTag.getCategoryID(), radio.getId());
                        LogUtil.d(TAG, "fav:" + radio.getFavorite());
                        item.setFav(radio.getFavorite());
                        item.setGroup(Constants.GROUP_RADIO);
                        item.setAudio(radio);
                        playListItems.add(item);
                    }

                    if (mCurrentAudioPlayListDetail == null) {
                        mCurrentAudioPlayListDetail = new PlayListDetail(currentPlayListType, playListItems);
                        //for audio play fragment,will get the   playlist detail in fragment
                        //because the  album playlist it different with the fragment list
                        // TODO
                        setCurrentAudio(0);
                        switchPlayFragment(getAndUpdateAudioPlayFragment(mcurrentAudio), mcurrentAudio.getAudioType() + "");
                        updatePlayControlFavUI();
                    } else {
                        mCurrentAudioPlayListDetail.addtoList(playListItems);
                        if (playListFragment != null) {
                            playListFragment.updateAll();
                        }
                    }
                    currentPage++;
                }

            }

        };
        AudioCenterHttpManager.getInstance(this).getRadiosByTAG(mloadDataSubscriber, currentTag.getRadioType(), currentTag.getProvince(), currentPage, PAGE_COUNT);
    }


    private void getFav() {
        LogUtil.d(TAG, "getFav: ");
        Subscriber<FavoriteResultVO> mloadDataSubscriber = new Subscriber<FavoriteResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(FavoriteResultVO tagResult) {

                if (tagResult == null && mCurrentAudioPlayListDetail == null) {
                    LogUtil.e(TAG, "getFav tagResult == null ");

                    switchPlayFragment(getStateFragment(PlayStateFragment.PLAYSTATE.NETERROR), STATEFRAGMENTTAG);

                } else if (tagResult.getFavorites().size() < 1 && mCurrentAudioPlayListDetail == null) {
                    LogUtil.e(TAG, "getFav getFavorites is null ");
                    switchPlayFragment(getStateFragment(PlayStateFragment.PLAYSTATE.DATANULL), STATEFRAGMENTTAG);

                } else {
                    //    public PlayListItem(int audioType, int categoryType, Long id) {

                    //group=0 music，group=1 album，group=2 radio
                    //map the fav and the radio ,album or music
                    List<PlayListItem> playListItems = new ArrayList<>();

                    int totalSize = tagResult.getFavorites().size();
                    LogUtil.d(TAG, "totalSize: " + totalSize);

                    for (int i = totalSize - 1; i > -1; i--) {
                        playListItems.add(decoratorFavoriteVO(tagResult.getFavorites().get(i)));
                    }
                    if (mCurrentAudioPlayListDetail == null) {
                        mCurrentAudioPlayListDetail = new PlayListDetail(currentPlayListType, playListItems);
                        //for audio play fragment,will get the   playlist detail in fragment
                        //because the  album playlist it different with the fragment list
                        // TODO
                        setCurrentAudio(0);
                        switchPlayFragment(getAndUpdateAudioPlayFragment(mcurrentAudio), mcurrentAudio.getAudioType() + "");
                        updatePlayControlFavUI();
                    } else {
                        //TODO   partial loading
                        //update the play list  data
                        mCurrentAudioPlayListDetail.addtoList(playListItems);
                        //  update the playfragment
                        if (playListFragment != null) {
                            playListFragment.updateAll();
                        }
                    }

                }
            }

        };
        AudioCenterHttpManager.getInstance(this).getFavorites(mloadDataSubscriber, currentPage, PAGE_COUNT);
        currentPage++;


    }

    private void getTAGAlbums(final TagDetail currentTag) {
        LogUtil.d(TAG, "getAlbums: " + currentTag.getCategoryID() + ":getName" + currentTag.getName());
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
                if (tagResult != null) {

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
                    if (mCurrentAudioPlayListDetail == null) {
                        mCurrentAudioPlayListDetail = new PlayListDetail(currentPlayListType, playListItems);
                        //for audio play fragment,will get the   playlist detail in fragment
                        //because the  album playlist it different with the fragment list
                        // TODO
                        setCurrentAudio(0);
                        switchPlayFragment(getAndUpdateAudioPlayFragment(mcurrentAudio), mcurrentAudio.getAudioType() + "");
                        updatePlayControlFavUI();
                    } else {
                        mCurrentAudioPlayListDetail.addtoList(playListItems);
                        if (playListFragment != null) {
                            playListFragment.updateAll();
                        }
                    }

                }
            }

        };
        AudioCenterHttpManager.getInstance(this).getAlbums(mloadDataSubscriber, currentTag.getCategoryID(), currentTag.getName() == null ? Constants.DEFULT_CROSS_TALK : currentTag.getName(), currentPage, PAGE_COUNT);
    }

    /**
     * the palylist also need the data,  must load the fragment(playtitem) data int the activity
     *
     * @param currentTag currentTag
     */
    private void getTAGMusics(final TagDetail currentTag) {
        LogUtil.d(TAG, "getTAGMusics: " + currentTag.getCategoryID() + ":getName" + currentTag.getName());
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
                if (tagResult != null) {
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
                            PlayListItem item = new PlayListItem(Constants.AUDIO_TYPE_MUSIC, currentTag.getCategoryID(), music.getId());
                            item.setAudio(music);
                            item.setFav(music.getFavorite());
                            item.setGroup(Constants.GROUP_MUSIC);
                            playListItems.add(item);
                            filterred.add(music);
                            LogUtil.d(TAG, "setPlayList :i:" + i + "" + music.getSong_name() + "" + music.getPlay_url());

                        }
                    }

                    if (mCurrentAudioPlayListDetail == null) {
                        mCurrentAudioPlayListDetail = new PlayListDetail(currentPlayListType, playListItems);
                        setCurrentAudio(0);
                        // TODO
                        //for audio play fragment   , the list of play fragment  is same with the palylist  ,use the id  of -1 means none
                        PlayListItem<List<MusicVO>> playdetail = new PlayListItem<List<MusicVO>>(Constants.AUDIO_TYPE_MUSIC, currentTag.getCategoryID(), -1l);
                        playdetail.setAudio(filterred);
                        //for audio play fragment end
                        switchPlayFragment(getAndUpdateAudioPlayFragment(playdetail), playdetail.getAudioType() + "");
                        updatePlayControlFavUI();
                    } else {
                        for (int i = 0; i < mCurrentAudioPlayListDetail.getPlayItemCount(); i++) {
                            //insert the old list to the front
                            filterred.add(i, (MusicVO) mCurrentAudioPlayListDetail.getPlayItem(i).getAudio());
                        }
                        mCurrentAudioPlayListDetail.addtoList(playListItems);
                        //for audio play fragment   , the list of play fragment  is same with the palylist  ,use the id  of -1 means none
                        PlayListItem<List<MusicVO>> playdetail = new PlayListItem<List<MusicVO>>(Constants.AUDIO_TYPE_MUSIC, currentTag.getCategoryID(), -1l);
                        playdetail.setAudio(filterred);
                        getAndUpdateAudioPlayFragment(playdetail);
                        if (playListFragment != null) {
                            playListFragment.updateAll();
                        }
                    }
                }
            }

        };
        AudioCenterHttpManager.getInstance(this).getMusics(mloadDataSubscriber, currentTag.getName(), currentPage, PAGE_COUNT);
    }


    private PlayListItem decoratorFavoriteVO(FavoriteVO fav) {
        PlayListItem playlistitem = null;
        LogUtil.d(TAG, "decoratorFavoriteVO  getId: " + fav.getId() + ",getAudio_id:" + fav.getAudio_id());
        switch (fav.getGroup()) {
            case Category.GROUP.MUSIC_GROUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_SINGLE_MUSIC, getCategoryIdFromName(fav.getCategory_name()), fav.getAudio_id());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                MusicVO music = new MusicVO();
                music.setPlay_url(fav.getPlay_url());
                music.setId(fav.getAudio_id());
                music.setSinger_name(fav.getAnnouncer());
                music.setSong_name(fav.getTitle());
                music.setAlbum_name(fav.getAnnouncer());
                music.setCover_url(fav.getCover_url());
                music.setCategory_id(getCategoryIdFromName(fav.getCategory_name()));
                playlistitem.setAudio(music);
                break;
            case Category.GROUP.OTHER_GOUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_ALBUM, getCategoryIdFromName(fav.getCategory_name()), fav.getAudio_id());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                AlbumVO album = new AlbumVO();
                album.setId(fav.getAudio_id());
                album.setTitle(fav.getTitle());
                album.setCover_url(fav.getCover_url());
                album.setCategory_id(getCategoryIdFromName(fav.getCategory_name()));
                playlistitem.setAudio(album);
                break;
            case Category.GROUP.RADIO_GROUP:
                playlistitem = new PlayListItem(Constants.AUDIO_TYPE_RADIO, getCategoryIdFromName(fav.getCategory_name()), fav.getAudio_id());
                playlistitem.setFav(Constants.FAV);
                playlistitem.setGroup(fav.getGroup());
                RadioVO radio = new RadioVO();
                radio.setPlay_url(fav.getPlay_url());
                radio.setId(fav.getAudio_id());
                radio.setName(fav.getTitle());
                radio.setDesc(fav.getAnnouncer());
                radio.setCover_url(fav.getCover_url());
                radio.setCategory_id(getCategoryIdFromName(fav.getCategory_name()));
                playlistitem.setAudio(radio);
                break;
            default:
                LogUtil.e(TAG, " error  group type not supported" + fav.getGroup());

        }
        return playlistitem;
    }


    private int getCategoryIdFromName(String name) {
        LogUtil.d(TAG, "name:" + name);
        int id = CategoryEnum.MUSIC.getId();
        for (CategoryEnum em : CategoryEnum.values()) {
            if (em.getName().equals(name)) {
                id = em.getId();
            }
        }
        LogUtil.d(TAG, "id:" + id);

        return id;
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
        int currentIndex = PlayListDetail.getCurrnIndex();
        if (currentIndex > 0) {
            currentIndex--;
        }
        setCurrentAudio(currentIndex);
        updateWholeView();
    }


    @OnClick(R.id.image_play_next)
    void playNext() {
        LogUtil.d(TAG, "playNext:");
        int currentIndex = PlayListDetail.getCurrnIndex();
        if (currentIndex < mCurrentAudioPlayListDetail.getPlayItemCount() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        if (mCurrentAudioPlayListDetail.getPlayItemCount() > 0) {
            setCurrentAudio(currentIndex);
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
//            playListFragment.addAllPlaylistData(mCurrentAudioPlayListDetail.getPlayList());
            ft.add(playListFragment, PLAYLIST).commit();
        }

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

                mcurrentAudio.updateFav();
                int contain = isCurrentPlaylistContain(mcurrentAudio.getId().longValue());

                LogUtil.d(TAG, "onNext mcurrentAudio: " + mcurrentAudio);
                LogUtil.d(TAG, "onNext mcurrentAudio: " + mCurrentAudioPlayListDetail.getPlayItemCount());
                LogUtil.d(TAG, "onNext contain: " + contain);
                if (contain > -1) {
                    mCurrentAudioPlayListDetail.setPlayItem(contain, mcurrentAudio);
                } else {
                    mCurrentAudioPlayListDetail.addtoList(mcurrentAudio);
                }
                //update the playlist
                //TODO if remove at the fav view ,must update the playlist ui
                updatePlayControlFavUI();

            }
        };
        LogUtil.d(TAG, "setFav  : " + mcurrentAudio.getId());

        if (mcurrentAudio.getFav() == Constants.UNFAV) {
            AudioCenterHttpManager.getInstance(this).addFavorite(mSetFavSubscriber, mcurrentAudio.getId(), mcurrentAudio.getCategoryType(), mcurrentAudio.getGroup());
        } else {
            AudioCenterHttpManager.getInstance(this).removeFavorite(mSetFavSubscriber, mcurrentAudio.getId(), mcurrentAudio.getCategoryType(), mcurrentAudio.getGroup());

        }
    }


    /**
     * @param position position
     */
    private void setCurrentAudio(int position) {
        LogUtil.d(TAG, "setCurrentAudio:" + position);
        mCurrentAudioPlayListDetail.setCurrnIndex(position);
        mcurrentAudio = mCurrentAudioPlayListDetail.getPlayItem(position);
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
            //TODO now tag and album is the same fragment
//            case Constants.AUDIO_TYPE_TAG:
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


    private void updatePlayFragment(int position) {
        PlayListItem item = mCurrentAudioPlayListDetail.getPlayItem(position);
        LogUtil.d(TAG, "updatePlayFragment:" + currentPlayListType);
        switch (currentPlayListType) {
            //FAV mode ,switch the fragment
            case Constants.PLAYLIST_FAV:
                //TODO  separate the fragment
                switchPlayFragment(getAndUpdateAudioPlayFragment(item), item.getAudioType() + "");
                updatePlayControlFavUI();
                break;
            case Constants.PLAYLIST_MUSIC:
                mCurrentPlayFragment.setInnerSellected(position);
                break;
            case Constants.PLAYLIST_RADIO:
                RadioVO radio = (RadioVO) item.getAudio();
                mCurrentPlayFragment.setPlaydata(radio);
                break;
            case Constants.PLAYLIST_ALBUM:
                AlbumVO album = (AlbumVO) item.getAudio();
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
    protected void onResume() {
        LogUtil.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart: ");
        super.onStart();
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
    public void updateListData() {
        LogUtil.d(TAG, "updateListData ::");
        updateAudioData();
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

    /**
     * @param audioId  audioId
     * @param favstate favstate
     * @return -1: current playlist do not  contain the audioId  ,else return the index in the playlist
     */
    private int updateTheFavState(long audioId, int favstate) {
        int containIndex = isCurrentPlaylistContain(audioId);
        if (containIndex != -1) {
            PlayListItem item = mCurrentAudioPlayListDetail.getPlayItem(containIndex);
            item.setFav(favstate);
            mCurrentAudioPlayListDetail.setPlayItem(containIndex, item);
        }
        return containIndex;
    }

    /**
     * @param audioId audioId
     * @return if contains ,replace the item and return the index,
     * else return -1
     */
    private int isCurrentPlaylistContain(long audioId) {
        for (int i = 0; i < mCurrentAudioPlayListDetail.getPlayItemCount(); i++) {
            PlayListItem item = mCurrentAudioPlayListDetail.getPlayItem(i);
            if (item.getId().longValue() == audioId) {
                return i;
            }
        }
        return -1;
    }

    private void updateWholeView() {
        updatePlayControlFavUI();
        updatePlayFragment(PlayListDetail.getCurrnIndex());
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


    private class MyPlayListManagerListener implements PlayListManagerListener {

        @Override
        public void initComplete(int resultcode, List<PlayListItem> resultmessage) {

        }

        @Override
        public void loadMoreComplete(int resultcode, List<PlayListItem> resultmessage) {

        }

        @Override
        public void onUpdate2ServerComplete(int resultcode, PlayListItem resultmessage) {

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
                    currenttime.setText(Utils.formatTime(currPos));
                    totaltime.setText(Utils.formatTime(duration));
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
