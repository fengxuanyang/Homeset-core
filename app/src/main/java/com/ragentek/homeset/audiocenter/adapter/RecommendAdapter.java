package com.ragentek.homeset.audiocenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/9.
 */

public class RecommendAdapter extends ListItemBaseAdapter<List<TagDetail>, RecommendAdapter.IndexViewHolder> {
    private static final String TAG = "RecommendAdapter";

    public RecommendAdapter(Context context, int initindex) {
        super(context, initindex);
    }

    public RecommendAdapter(Context context) {
        super(context);
    }

    @Override
    public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.audioenter_recommend_item, parent, false);
        return new IndexViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final IndexViewHolder holder, int position) {
        LogUtil.d(TAG, "" + position);
//        holder.image.setImageResource(R.drawable.placeholder_disk);
        holder.image.setImageResource(mData.get(position).getIcon());
        holder.tv.setText(mData.get(position).getName());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            });

        }
        return;
    }

    @Override
    public void addDatas(List<TagDetail> data) {
        LogUtil.d(TAG, "setDatas: " + data.size());
        super.addDatas(data);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else return 0;
    }

    public class IndexViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_index)
        SimpleDraweeView image;

        @BindView(R.id.tv_index)
        TextView tv;

        public IndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
