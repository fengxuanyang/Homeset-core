package com.ragentek.homeset.ui.launcher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class PageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> mFragments;

    public PageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);

        mFragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}