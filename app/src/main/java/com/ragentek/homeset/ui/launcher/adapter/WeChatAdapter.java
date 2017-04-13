package com.ragentek.homeset.ui.launcher.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.wechat.domain.WeChatInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenjin.wang on 2017/3/30.
 */

public class WeChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{
    private static final String TAG = "WeChatAdapter";

    private Context mContext;
    private List<WeChatInfo> mData;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //自定义监听事件
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }

    public WeChatAdapter(Context context) {
        this.mContext = context;
        this.mData = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.wechat_item, parent, false);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder item = (ItemViewHolder)holder;
        ImageView icon = item.getIcon();
        TextView name = item.getName();

        if(mData.get(position).getIconUrl() != null && mData.get(position).getIconUrl().length() > 10){
            icon.setImageURI(Uri.parse(mData.get(position).getIconUrl() ));
            Glide.with(mContext)
                 .load(mData.get(position)
                 .getIconUrl())
                 .error(R.drawable.contact_default)
                 .diskCacheStrategy(DiskCacheStrategy.RESULT)
                  .into(icon);//加载网络图片
        } else {
            icon.setImageResource(R.drawable.contact_default);
        }

        Log.d(TAG, "mData.get(position):"+mData.get(position).toString());
        if(mData.get(position).getConRemark() != null && mData.get(position).getConRemark().length() > 0 ) {
            name.setText(mData.get(position).getConRemark());
        }else if(mData.get(position).getNickName() != null && mData.get(position).getNickName().length() > 0){
            name.setText(mData.get(position).getNickName());
        }else if(mData.get(position).getAlias() != null && mData.get(position).getAlias().length() > 0){
            name.setText(mData.get(position).getAlias());
        }else{
            name.setText(mData.get(position).getUserName());
        }
        item.setUserName(mData.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener!= null) {
            mOnItemClickListener.onItemLongClick(v);
        }
        return false;
    }

    public List<WeChatInfo> getData() {
        return mData;
    }

    public void setData(List<WeChatInfo> mData) {
        if(mData != null) {
            this.mData.clear();
            this.mData.addAll(mData);
        }

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView name;
        private String userName;

        public ItemViewHolder(View view) {
            super(view);
            icon = (ImageView)view.findViewById(R.id.iv_conact);
            name = (TextView) view.findViewById(R.id.tv_conact_name);
        }

        public ImageView getIcon() {
            return icon;
        }

        public void setIcon(ImageView icon) {
            this.icon = icon;
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
