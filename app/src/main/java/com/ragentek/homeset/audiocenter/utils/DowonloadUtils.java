package com.ragentek.homeset.audiocenter.utils;

import android.content.Context;
import android.content.Intent;

import com.ragentek.homeset.audiocenter.service.DownloadService;

/**
 * Created by xuanyang.feng on 2017/3/2.
 */

public class DowonloadUtils {
    private static DowonloadUtils mDowonloadUtils;
    private Context mContext;

    private DowonloadUtils(Context context) {
        mContext = context;
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.startService(intent);
    }

    public static DowonloadUtils getInstance(Context context) {
        if (mDowonloadUtils == null) {
            synchronized (DowonloadUtils.class) {
                if (mDowonloadUtils == null) {
                    mDowonloadUtils = new DowonloadUtils(context.getApplicationContext());
                }
            }

        }
        return mDowonloadUtils;
    }

}
