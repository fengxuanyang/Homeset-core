package com.ragentek.homeset.audiocenter.view.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.ragentek.homeset.audiocenter.utils.LogUtil;


/**
 * Created by xuanyang.feng on 2017/4/6.
 */

public abstract class RecycleViewEndlessOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "RecycleViewEndlessOnScr";
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutManagerType layoutManagerType;
    private int currentPage = 0;
    private int[] lastPositions;
    private int previousTotal = 0;
    private int lastVisibleItemPosition;
    private int currentScrollState = 0;
    private boolean loading = false;


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        LogUtil.d(TAG, "onScrollStateChanged: ");

        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        currentScrollState = newState;
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        } else if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                !loading && lastVisibleItemPosition + 1 == totalItemCount && totalItemCount - visibleItemCount <= lastVisibleItemPosition) {
            LogUtil.d(TAG, "onScrollStateChanged  onLoadMore: ");
            loading = true;
            onLoadMore(currentPage);
            currentPage++;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayout:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }


    }

    /**
     * for gridview
     *
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public abstract void onLoadMore(int currentPage);

    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }
}
