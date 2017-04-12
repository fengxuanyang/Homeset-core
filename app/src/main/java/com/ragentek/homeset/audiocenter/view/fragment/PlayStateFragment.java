package com.ragentek.homeset.audiocenter.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/4/11.
 */

public class PlayStateFragment extends PlayBaseFragment<PlayStateFragment.PLAYSTATE> {


    @BindView(R.id.tv_sate_view)
    TextView stateTextView;
    private PLAYSTATE currentState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate: " + this);
        View view = inflater.inflate(R.layout.fragment_audioenter_playstate_layout, container, false);
        ButterKnife.bind(this, view);
        updateView(currentState);
        return view;
    }


    private void updateData() {

    }

    @Override
    public void setInnerSellected(int index) {
    }

    @Override
    void onDataChanged(PLAYSTATE playdata) {
        currentState = playdata;
        if (isVisible()) {
            updateView(playdata);
        }
    }


    private void updateView(PLAYSTATE playdata) {
        StringBuilder sb = new StringBuilder();
        switch (playdata) {
            case NETERROR:
                sb.append(getContext().getResources().getString(R.string.net_error));
                break;
            case DATANULL:
                sb.append(getContext().getResources().getString(R.string.none_fav_data));
                break;
            case DATAERROR:
                sb.append(getContext().getResources().getString(R.string.data_error));

                break;
        }
        stateTextView.setText(sb);
    }

    public enum PLAYSTATE {
        NETERROR,
        DATANULL,
        DATAERROR;
    }
}
