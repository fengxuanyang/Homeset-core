package com.ragentek.homeset.audiocenter.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.MusicListAdapter;
import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.RecycleItemDecoration;
import com.ragentek.homeset.audiocenter.view.widget.RecycleViewEndlessOnScrollListener;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.MusicVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/14.
 * for  the  category of music
 */
public class MusicFragment extends PlayBaseFragment<List<MusicVO>> {
    private static final String TAG = "MusicFragment";
    private ListItemBaseAdapter mTrackListAdapter;
    private int currentPlayIndex = 0;

    @BindView(R.id.tv_album_title)
    TextView mAlbumTitle;
    @BindView(R.id.image_album)
    SimpleDraweeView mSimpleDraweeView;
    @BindView(R.id.rv_album_playlist)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_album_load)
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_album_detail, container, false);
        ButterKnife.bind(this, view);
        inteView();
        if (getPlaydata() != null) {
            currentPlayIndex = 0;
            loadData();
            updateTitle();
            updateAlbumart();
        }
        return view;
    }


    @Override
    public void setInnerSellected(int index) {
        currentPlayIndex = index;
        updateTitle();
        mTrackListAdapter.updateSellect(currentPlayIndex);
        control.play(index);
    }

    @Override
    void onDataChanged(List<MusicVO> playdata) {
        if (isVisible()) {
            updateTitle();
            updateAlbumart();
            loadData();
        }
    }


    private void updateTitle() {
        LogUtil.d(TAG, "updateTitle: ");
        mAlbumTitle.setText(playdata.get(currentPlayIndex).getAlbum_name());
        mAlbumTitle.getPaint().setFakeBoldText(true);
    }

    private void inteView() {
        mTrackListAdapter = new MusicListAdapter(this.getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new RecycleItemDecoration(getActivity(), RecycleItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTrackListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.d(TAG, "onItemClick: " + position);
                currentPlayIndex = position;
                control.play(position);
                mTrackListAdapter.updateSellect(position);
            }
        });

        mRecyclerView.setAdapter(mTrackListAdapter);
        mRecyclerView.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                PlayListFragment.PlayListListener listListener = (PlayListFragment.PlayListListener) getActivity();
                listListener.updateListData();
            }
        });
    }


    private void updateAlbumart() {
        String cover = playdata.get(currentPlayIndex).getCover_url();
        LogUtil.d(TAG, "updateAlbumart coverUri: " + cover);
        if (cover == null) {
            mSimpleDraweeView.setImageResource(R.drawable.placeholder_disk);
        } else {
            mSimpleDraweeView.setImageURI(Uri.parse(cover));
        }
    }

    private void loadData() {
        LogUtil.d(TAG, "loadData: ");
        mProgressBar.setVisibility(View.VISIBLE);
        mTrackListAdapter.setDatas(playdata);
        List<PlayItem> list = new ArrayList<>();
        for (int i = 0; i < playdata.size(); i++) {
            MusicVO musicvo = playdata.get(i);
            PlayItem item = new PlayItem();
            item.setPlayUrl(musicvo.getPlay_url());
            item.setCoverUrl(musicvo.getCover_url());
            item.setTitle(musicvo.getSong_name());
            list.add(item);
        }
        control.setPlayList(list, currentPlayIndex);
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate: " + this);
    }


    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, "onDetach: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: " + this);
    }
}

