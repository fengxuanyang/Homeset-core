package com.ragentek.homeset.audiocenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.model.bean.PlayItem;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.TrackVO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/10.
 */

public class TrackListAdapter extends ListItemBaseAdapter<List<PlayItem>, TrackListAdapter.AlbumItemAdapterViewHolder> {
    private static final String TAG = "TrackListAdapter";


    public TrackListAdapter(Context context) {
        super(context);
        LogUtil.d(TAG, "TrackListAdapter: ");
    }

    @Override
    public TrackListAdapter.AlbumItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.audioenter_track_item, parent, false);
        return new TrackListAdapter.AlbumItemAdapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final TrackListAdapter.AlbumItemAdapterViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder  position : " + this.getData().get(position).getTitle());
        holder.itemName.setText(this.getData().get(position).getTitle());
        holder.itemIndex.setText("" + (position + 1));
        if (curSellect == position) {
            holder.itemName.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            holder.itemIndex.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
        } else {

            holder.itemName.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
            holder.itemIndex.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition(); // 1
                mOnItemClickListener.onItemClick(holder.itemView, position); // 2
            }
        });
    }


    @Override
    public int getItemCount() {
        LogUtil.d(TAG, "getItemCount ");
        if (mData != null) {
            return mData.size();

        } else return 0;
    }

    public class AlbumItemAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_item_name)
        TextView itemName;
        @BindView(R.id.album_item_index)
        TextView itemIndex;

        public AlbumItemAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
