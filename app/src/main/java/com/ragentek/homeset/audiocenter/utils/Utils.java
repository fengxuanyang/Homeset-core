package com.ragentek.homeset.audiocenter.utils;

import android.os.Environment;

/**
 * Created by xuanyang.feng on 2017/3/7.
 */

public class Utils {


    private static final int ONE_MIN = 1 * 60 * 1000;

    private static final int ONE_SECOND = 1 * 1000;

    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int min = (int) (ms / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }
}
