package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.PlayListAdapter;
import com.ragentek.homeset.audiocenter.model.bean.PlayListDetail;
import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.ImageWithText;
import com.ragentek.homeset.audiocenter.view.widget.RecycleViewEndlessOnScrollListener;
import com.ragentek.homeset.core.HomesetApp;
import com.ragentek.homeset.core.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xuanyang.feng on 2017/3/13.
 * view for the playlist
 */

public class PlayListFragment extends DialogFragment {
    public static final String TAG_PLAYINDEX = "playindex";

    private static final String TAG = "PlayListFragment";
    public Activity mContext;
    private PlayListAdapter mPlayListAdapter;
    //TODO is needed to  keep list  move it
    private PlayListListener mPlayListListener;
    private List<PlayListItem> currentPlaylist;
    private int playindex;

    @BindView(R.id.rv_playlist)
    RecyclerView playlistRV;
    @BindView(R.id.tv_close)
    TextView closeTextView;
    @BindView(R.id.tv_listname)
    TextView listNameTextView;
    @BindView(R.id.swiperefresh_playlist)
    SwipeRefreshLayout swipeRefresh;


    @Override
    public void onAttach(Activity activity) {
        LogUtil.d(TAG, "onAttach: ");
        super.onAttach(activity);
        this.mContext = activity;
        mPlayListListener = (PlayListListener) activity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomDialog);
        Bundle argument = getArguments();
        if (argument != null) {
            playindex = argument.getInt(TAG_PLAYINDEX);
        }
    }


    public void setCurrentPlayIndext(int index) {
        LogUtil.d(TAG, "setCurrentPlayIndext: " + index);
        playindex = index;
        if (isVisible()) {
            mPlayListAdapter.updateSellect(playindex);
        }
    }


    public void addData(List<PlayListItem> list) {
        LogUtil.d(TAG, "addData: " + isVisible());
        if (currentPlaylist == null) {
            currentPlaylist = new ArrayList<>();
        }
        currentPlaylist.addAll(list);
        showData();
    }

    private void showData() {
        LogUtil.d(TAG, "updateAll: ");
        if (isVisible()) {
            mPlayListAdapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }
    }

    /**
     * @param index index > =0 .means ,current playlist contains the index .so the action is add
     *              else  is remove
     * @param isFac
     */
    public void playListNumChanger(int index, boolean isFac) {
        LogUtil.d(TAG, "playListNumChanger: " + index + ",current:" + PlayListDetail.getCurrnIndex());
        if (isVisible()) {
            mPlayListAdapter.notifyDataSetChanged();
        }
        if (isFac) {
            //index >-1 ,in fav means remove
            if (index > -1) {
                mPlayListAdapter.updateSellect(index);

            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.audioenter_fragment_dialog_playlist, container);
        ButterKnife.bind(this, view);
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().setAttributes(params);
        initView();
        return view;
    }

    private void initView() {
        LogUtil.d(TAG, "initView: ");
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.d(TAG, "onRefresh: ");
            }
        });
        mPlayListAdapter = new PlayListAdapter(mContext, playindex);
        if (currentPlaylist != null && currentPlaylist.size() > 0) {
            mPlayListAdapter.addDatas(currentPlaylist);
        }
        playlistRV.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        playlistRV.setLayoutManager(mLayoutManager);
        playlistRV.setAdapter(mPlayListAdapter);
        mPlayListAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //update the playlist view
                mPlayListAdapter.updateSellect(position);
                mPlayListListener.onItemClick(position);

            }
        });

        playlistRV.addOnScrollListener(new RecycleViewEndlessOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                mPlayListListener.onLoadMore();
                swipeRefresh.setRefreshing(true);
            }
        });
    }

    @OnClick(R.id.tv_close)
    void closeFragment() {
        LogUtil.d(TAG, "closeFragment: ");

        if (mPlayListListener != null) {
            mPlayListListener.onCloseClick();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.8);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }


    public interface PlayListListener {
        void onItemClick(int position);

        void onCloseClick();

        void onFavClick(int position);

        void onLoadMore();

    }


}
