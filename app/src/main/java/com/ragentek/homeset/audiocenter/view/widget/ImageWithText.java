package com.ragentek.homeset.audiocenter.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ragentek.homeset.core.R;

/**
 * Created by xuanyang.feng on 2017/3/31.
 */

public class ImageWithText extends LinearLayout {
    public ImageView imageView;
    public TextView textView;

    public ImageWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        int picture_id = a.getResourceId(R.styleable.ImageTextView_imagesrc, -1);
        int text_id = a.getResourceId(R.styleable.ImageTextView_text, -1);
        int text_colorid = a.getResourceId(R.styleable.ImageTextView_textColor,getResources().getColor(R.color.colorWhite));
        a.recycle();
        imageView = new ImageView(context, attrs);
        imageView.setPadding(10, 10, 10, 10);

        imageView.setImageResource(picture_id);
        textView = new TextView(context, attrs);

        textView.setText(text_id);
        textView.setTextColor(text_colorid);
        textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        textView.setPadding(0, 0, 0, 0);
        setClickable(true);
        setFocusable(true);
        setOrientation(LinearLayout.VERTICAL);
        addView(imageView);
        addView(textView);
    }


    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }


    public void setTextResource(int resId) {
        textView.setText(resId);
    }


    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
}
