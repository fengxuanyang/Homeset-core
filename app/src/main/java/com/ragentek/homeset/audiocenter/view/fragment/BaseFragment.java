package com.ragentek.homeset.audiocenter.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by xuanyang.feng on 2017/2/8.
 */

public class BaseFragment extends Fragment {
    protected static String TAG;

    public Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = getClass().getSimpleName();
        mActivity = (Activity) context;
    }


}
