package com.ragentek.homeset.audiocenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.model.bean.PlayListItem;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.AlbumVO;
import com.ragentek.protocol.commons.audio.MusicVO;
import com.ragentek.protocol.commons.audio.RadioVO;
import com.ragentek.protocol.constants.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuanyang.feng on 2017/3/10.
 */

public class PlayListAdapter extends ListItemBaseAdapter<List<PlayListItem>, PlayListAdapter.PlayListItemAdapterViewHolder> {
    private static final String TAG = "PlayListAdapter";

    public PlayListAdapter(Context context, int index) {
        super(context, index);
    }


    @Override
    public PlayListAdapter.PlayListItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.audioenter_playlist_item, parent, false);

        return new PlayListAdapter.PlayListItemAdapterViewHolder(itemView);
    }

    //match the different view item
    @Override
    public int getItemViewType(int position) {
        PlayListItem item = this.getData().get(position);
        return item.getAudioType();
    }

    @Override
    public void onBindViewHolder(final PlayListAdapter.PlayListItemAdapterViewHolder holder, int position) {
        PlayListItem item = this.getData().get(position);

        int audiotype = item.getAudioType();
        LogUtil.d(TAG, "onBindViewHolder  audiotype: " + audiotype);

        String category = mContext.getResources().getString(R.string.music);
        switch (item.getCategoryType()) {
            //TODO is the same ui ?
            case Category.ID.CROSS_TALK:
                category = mContext.getResources().getString(R.string.cross_talk);
                break;
            case Category.ID.CHINA_ART:
                category = mContext.getResources().getString(R.string.china_art);
                break;
            case Category.ID.HEALTH:
                category = mContext.getResources().getString(R.string.health);
                break;
            case Category.ID.STORYTELLING:
                category = mContext.getResources().getString(R.string.storytelling);
                break;
            case Category.ID.STOCK:
                category = mContext.getResources().getString(R.string.stock);
                break;
            case Category.ID.HISTORY:
                category = mContext.getResources().getString(R.string.history);
                break;
            case Category.ID.RADIO:
                category = mContext.getResources().getString(R.string.radio);
                break;
            case Category.ID.MUSIC:
                category = mContext.getResources().getString(R.string.music);
                break;
        }

        switch (audiotype) {
            case Constants.AUDIO_TYPE_ALBUM:
                AlbumVO album = (AlbumVO) item.getAudio();
                Log.d(TAG, "onBindViewHolder  position : " + album.getTitle());
                holder.itemAlbum.setText(album.getTitle());
                break;
            case Constants.AUDIO_TYPE_MUSIC:
            case Constants.AUDIO_TYPE_SINGLE_MUSIC:
                MusicVO music = (MusicVO) item.getAudio();
                holder.itemAlbum.setText(music.getSong_name());
                break;
            case Constants.AUDIO_TYPE_RADIO:
                RadioVO radio = (RadioVO) item.getAudio();
                LogUtil.d(TAG, "radio: getName " + radio.getName());
                holder.itemAlbum.setText(radio.getName());
                break;
        }

        if (item.getFav() == Constants.FAV) {
            holder.itemFavorate.setImageResource(R.drawable.my_fav);
        } else {
            holder.itemFavorate.setImageResource(R.drawable.my_unfav);
        }
        if (curSellect == position) {
            holder.itemCategory.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            holder.itemIndext.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            holder.itemAlbum.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
        } else {
            holder.itemAlbum.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
            holder.itemCategory.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
            holder.itemIndext.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
        }
        holder.itemCategory.setText(category);
        holder.itemIndext.setText((position + 1) + "");
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
            LogUtil.d(TAG, "getItemCount " + mData.size());

            return mData.size();

        } else return 0;
    }


    public class PlayListItemAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_playlist_index)
        TextView itemIndext;
        @BindView(R.id.tv_playlist_albumname)
        TextView itemAlbum;
        @BindView(R.id.tv_playlist_category)
        TextView itemCategory;
        @BindView(R.id.iv_playlist_isfav)
        ImageView itemFavorate;


        public PlayListItemAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
