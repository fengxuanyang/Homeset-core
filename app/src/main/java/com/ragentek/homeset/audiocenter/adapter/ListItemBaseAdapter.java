package com.ragentek.homeset.audiocenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/2/23.
 */

public abstract class ListItemBaseAdapter<T extends List, R extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<R> {
    private static final String TAG = "ListItemBaseAdapter";
    OnItemClickListener mOnItemClickListener;
    T mData;
    Context mContext;
    int curSellect = 0;

    public ListItemBaseAdapter(Context context, int index) {
        curSellect = index;
    }

    public void updateSellect(int index) {
        int preSellect = curSellect;
        curSellect = index;
        notifyItemChanged(preSellect);
        notifyItemChanged(curSellect);
    }

    public ListItemBaseAdapter(Context context) {
        mContext = context;
    }


    /**
     * @param data
     * @param start start of items to ba added
     */
    public void insertDatas(T data, int start) {
        LogUtil.d(TAG, "insertDatas: " + start + ",size" + data.size());
        if (mData == null) {
            mData = data;
        } else {
            LogUtil.d(TAG, ",size" + mData.size());
            mData.addAll(start, data);
        }
        LogUtil.d(TAG, ",size" + mData.size());

        notifyDataSetChanged();
    }

    public void addDatas(T date) {
        LogUtil.d(TAG, "addDatas: " + date.size());

        if (mData == null) {
            mData = date;
        } else {
            mData.addAll(date);
        }
        notifyDataSetChanged();
    }


    /**
     * new  data
     * @param data
     */
    public void setDatas(T data) {
        LogUtil.d(TAG, "setDatas: " + data.size());
        mData = data;
        //re init curSellect ,start play from the top
        curSellect = 0;
        notifyDataSetChanged();
    }

    public void removeDate(int index) {
        LogUtil.d(TAG, "removeDate: " + index);

        if (mData != null) {
            mData.remove(index);
        }
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public T getData() {
        return mData;
    }

    public int getStart() {
        return mData.size();
    }
}
