package com.ragentek.homeset.audiocenter.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by xuanyang.feng on 2017/3/8.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static File creatFile(String filePath, String name) {
        LogUtil.d(TAG, " creatFile  path:" + filePath + ",fileName:" + name);
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(filePath, name);
        try {
            if (!file.createNewFile()) {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
