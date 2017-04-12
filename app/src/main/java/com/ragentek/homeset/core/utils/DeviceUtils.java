package com.ragentek.homeset.core.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileInputStream;

public class DeviceUtils {
    private static final String TAG = "DeviceUtils";

    private static final String SN_FILE = "sn.txt";
    private static final String SN = "2017032803";

    public static final String SHARED_PREFERENCES_NAME = "account_pref";
    public static final String SP_REGISTER = "register";
    public static final String SP_LOGIN = "login";
    public static final String SP_UID = "uid";
    public static final String SP_DID = "did";
    public static final String SP_QRTICKET = "qrticket";
    public static final String SP_ATOKEN = "access_token";
    public static final String SP_RTOKEN = "refresh_token";

    private Context mContext;
    private SharedPreferences mSP;

    private static DeviceUtils mInstance;

    public static DeviceUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceUtils(context);
        }
        return mInstance;
    }

    public DeviceUtils(Context context) {
        mContext = context;
        mSP = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private String getSPValue(String key, String val) {
        return mSP.getString(key, val);
    }

    private void setSPValue(String key, String val) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public String getSN() {
        String sn = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), SN_FILE);
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            sn = new String(b);
            LogUtils.d(TAG, "getSN, sn from file, sn=" + sn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sn == null) {
            sn = SN;
            LogUtils.d(TAG, "getSN, sn from default, sn=" + sn);
        }

        return sn;
    }

    public String getWifiMac() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        if (wifiMac == null) {
            wifiMac = "02:00:00:00:00:00";
            LogUtils.d(TAG, "getWifiMac, default wifiMac=" + wifiMac);
        } else {
            LogUtils.d(TAG, "getWifiMac, real wifiMac=" + wifiMac);
        }
        return wifiMac;
    }

    public String getBtMac() {
        String btMac = android.provider.Settings.Secure.getString(mContext.getContentResolver(), "bluetooth_address");
        if (btMac == null) {
            btMac = "02:00:00:00:00:00";
            LogUtils.d(TAG, "getBtMac, default btMac=" + btMac);
        } else {
            LogUtils.d(TAG, "getBtMac, real btMac=" + btMac);
        }
        return btMac;
    }

    public String getHwVer() {
        return "hw_1.0";
    }

    public String getFwVer() {
        return "fw_1.0";
    }

    public String getSwVer() {
        return "sw_1.0";
    }

    public String getHwPfVer() {
        String hwPfVer = android.os.Build.DEVICE;
        LogUtils.d(TAG, "getHwPfVer, hwPfVer=" + hwPfVer);
        return hwPfVer;
    }

    public String getSwPfVer() {
        String swPfVer = android.os.Build.VERSION.RELEASE;
        LogUtils.d(TAG, "getSwPfVer, swPfVer=" + swPfVer);
        return swPfVer;
    }

    public String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String imei = "0123456789";
        try {
            imei = telephonyManager.getDeviceId();
            if (imei == null) {
                imei = "0123456789";
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "getImei, imei=" + imei);
        return imei;
    }

    public String getImsi() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String imsi = "0123456789";
        try {
            imsi = telephonyManager.getSubscriberId();
            if (imsi == null) {
                imsi = "0123456789";
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "getImsi, imsi=" + imsi);
        return imsi;
    }

    public Long getTimeStamp() {
        long timeStamp = System.currentTimeMillis();
        LogUtils.d(TAG, "getTimeStamp, timeStamp=" + timeStamp);
        return timeStamp;
    }

    public String getSign() {
        return "ksiuiu123iusdfyp123uoi";
    }

    public void setUid(String uid) {
        setSPValue(SP_UID, uid);
    }

    public String getUid() {
        return getSPValue(SP_UID, "0");
    }

    public void setDid(String did) {
        setSPValue(SP_DID, did);
    }

    public String getDid() {
        return getSPValue(SP_DID, "0");
    }

    public void setAccessToken(String accessToken) {
        setSPValue(SP_ATOKEN, accessToken);
    }

    public String getAccessToken() {
        return getSPValue(SP_ATOKEN, "0");
    }

    public void setRefreshToken(String refreshToken) {
        setSPValue(SP_RTOKEN, refreshToken);
    }

    public String getRefreshToken() {
        return getSPValue(SP_RTOKEN, "0");
    }

    public void setQrticket(String qrticket) {
        setSPValue(SP_QRTICKET, qrticket);
    }

    public String getQrticket() {
        return getSPValue(SP_QRTICKET, "0");
    }

    public void setRegisterFlag(String flag) {
        setSPValue(SP_REGISTER, flag);
    }

    public String getRegisterFlag() {
        return getSPValue(SP_REGISTER, "0");
    }

    public void setLoginFlag(String flag) {
        setSPValue(SP_LOGIN, flag);
    }

    public String getLoginFlag() {
        return getSPValue(SP_LOGIN, "0");
    }

    public long getLUid() {
        String uid = getSPValue(SP_UID, "0");
        return Long.parseLong(uid);
    }

    public long getLDid() {
        String did = getSPValue(SP_DID, "0");
        return Long.parseLong(did);
    }
}
