package com.ragentek.homeset.ui.launcher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by xuanyang.feng on 2017/2/23.
 */

public abstract class ListItemBaseAdapter<T extends List, R extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<R> {
    private static final String TAG = "ListItemBaseAdapter";
    OnItemClickListener mOnItemClickListener;
    T mData;
    Context mContext;

    public ListItemBaseAdapter(Context context) {
        mContext = context;
    }

    public void setDatas(T data) {
        mData = data;
        Log.d(TAG, "setDatas: " + mData);
        notifyDataSetChanged();
    }

    public void addDatas(T date) {
        if (mData == null) {
            mData = date;
        } else {
            mData.addAll(date);
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