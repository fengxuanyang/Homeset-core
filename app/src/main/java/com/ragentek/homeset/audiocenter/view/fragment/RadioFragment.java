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
import com.ragentek.protocol.commons.audio.RadioVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * for  the  category of radio;
 */

public class RadioFragment extends PlayBaseFragment<RadioVO> {
    private static final String TAG = "MusicFragment";
    private int currentPlayIndex = 0;

    @BindView(R.id.tv_radio_name)
    TextView radioNameTV;


    @BindView(R.id.image_radio_album)
    SimpleDraweeView mSimpleDraweeView;

    @BindView(R.id.progress_music_load)
    ProgressBar mProgressBar;


    @Override
    public void setInnerSellected(int index) {
        currentPlayIndex = index;
        updateData();
        control.play(index);
    }

    @Override
    void onDataChanged(RadioVO playdata) {
        updateData();
        updateView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate: " + this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audioenter_fragment_radio_detail, container, false);
        ButterKnife.bind(this, view);
        updateView();
        updateData();
        return view;
    }

    private void updateData() {
        List<PlayItem> list = new ArrayList<>();
        PlayItem item = new PlayItem();
        item.setPlayUrl(playdata.getPlay_url());
        item.setCoverUrl(playdata.getCover_url());
        item.setTitle(playdata.getName());
        list.add(item);
        control.setPlayList(list, 0);
    }

    private void updateView() {
        radioNameTV.setText(playdata.getName());
        updateAlbumart();
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
}
