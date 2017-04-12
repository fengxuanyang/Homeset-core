package com.ragentek.homeset.wechat;

import android.util.Log;

import com.ragentek.homeset.core.utils.SystemPropertiesInvoke;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * Created by wenjin.wang on 2017/1/17.
 */

public class WeChatUtils {
    private static final String TAG = "WeChatUtils";

    public static void exec(String cmd){
        try{
            SystemPropertiesInvoke.start(cmd);
            Thread.sleep(600);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * md5加密
     *
     * @param content
     * @return
     */
    public static String md5(String content) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
