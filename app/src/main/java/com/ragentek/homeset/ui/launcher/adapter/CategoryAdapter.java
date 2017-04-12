package com.ragentek.homeset.ui.launcher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.core.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuanyang.feng on 2017/3/10.
 */

public class CategoryAdapter extends ListItemBaseAdapter<List<CategoryDetail>, CategoryAdapter.CategoryAdapterViewHolder> {
    private static final String TAG = "CategoryAdapter";
    List<Integer> colorArray = new ArrayList<>();
    int mColorIdx = 0;

    public CategoryAdapter(Context context) {
        super(context);
        colorArray.add(R.color.colorYellow);
        colorArray.add(R.color.colorPurple);
        colorArray.add(R.color.colorGreen);
        colorArray.add(R.color.colorBlue);
        colorArray.add(R.color.colorPink);
        colorArray.add(R.color.colorOrange);
    }

    @Override
    public CategoryAdapter.CategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.CategoryAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.CategoryAdapterViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + mData.size() + " position=" + position);
        Log.d(TAG, "onBindViewHolder: " + position);

        if (mColorIdx >= colorArray.size()) {
            mColorIdx = 0;
        }

        holder.categorylayout.setBackgroundColor(mContext.getResources().getColor(colorArray.get(mColorIdx++)));
        holder.categoryName.setText(mData.get(position).getName());
        holder.categoryImage.setImageResource(mData.get(position).getIcon());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount ");
        if (mData != null) {
            return mData.size();

        } else return 0;
    }

    public class CategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;
        RelativeLayout categorylayout;

        public CategoryAdapterViewHolder(View itemView) {
            super(itemView);

            categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
            categoryImage = (ImageView) itemView.findViewById(R.id.iv_category);
            categorylayout = (RelativeLayout) itemView.findViewById(R.id.linearlayout_category);
        }
    }
}