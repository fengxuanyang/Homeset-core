package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * * for  the  favorite of  music
 */
public class SingleMusicFragment extends PlayBaseFragment<MusicVO> {
    private static final String TAG = "SingleMusicFragment";
    @BindView(R.id.tv_music_album)
    TextView albumText;
    @BindView(R.id.tv_music_singer)
    TextView singerText;
    @BindView(R.id.tv_music_name)
    TextView musicName;

    @BindView(R.id.image_music_album)
    SimpleDraweeView mSimpleDraweeView;

    @BindView(R.id.progress_music_load)
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate: " + this);

        View view = inflater.inflate(R.layout.audioenter_fragment_music_detail, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        updateAlbumart();
        updateData();
    }

    private void updateData() {

        LogUtil.d(TAG, "updateAlbumart getCover_url: " + playdata.getCover_url());
        LogUtil.d(TAG, "updateAlbumart getSong_name: " + playdata.getSong_name());
        LogUtil.d(TAG, "updateAlbumart getPlay_url: " + playdata.getPlay_url());
        LogUtil.d(TAG, "updateAlbumart getId: " + playdata.getId());
        LogUtil.d(TAG, "updateAlbumart getAlbum_name: " + playdata.getAlbum_name());

        musicName.setText(playdata.getSong_name());
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        item.setPlayUrl(playdata.getPlay_url());
        item.setCoverUrl(playdata.getCover_url());
        item.setTitle(playdata.getSong_name());
        list.add(item);
        control.setPlayList(list, 0);
    }


    private void updateAlbumart() {
        if (playdata.getCover_url() == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(playdata.getCover_url()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy: " + this);

        super.onDestroy();
    }

    @Override
    public void setInnerSellected(int index) {

    }

    @Override
    public void onDataChanged(MusicVO playdata) {

    }
}
