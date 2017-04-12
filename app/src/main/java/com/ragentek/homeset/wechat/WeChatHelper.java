package com.ragentek.homeset.wechat;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ragentek.homeset.core.utils.SystemPropertiesInvoke;
import com.ragentek.homeset.wechat.domain.WeChatException;
import com.ragentek.homeset.wechat.domain.WeChatInfo;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenjin.wang on 2017/1/17.
 */

public class WeChatHelper {

    private static final String TAG = WeChatHelper.class.getSimpleName();
    private static final String DB_NAME = "EnMicroMsg.db";
    private static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
    private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";
    private static final String WX_SP_UIN_PATH = WX_ROOT_PATH + "shared_prefs/auth_info_key_prefs.xml";

    private String mDbPassword;
    private String mPhoneIMEI;
    private String mCurrWxUin;
    private Context mContext;
    private SQLiteDatabaseHook hook;
    private SqlCipherHelper mSqlHelper;

    public WeChatHelper(Context context) {
        hook = new SQLiteDatabaseHook() {
            public void preKey(SQLiteDatabase database) {
            }

            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA cipher_migrate;"); //兼容2.0的数据库
            }
        };
        mContext = context;
        SQLiteDatabase.loadLibs(mContext);
        initPhoneIMEI();
    }

    public void open() throws WeChatException {
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "opne startTime:"+ startTime);
        close();

        StringBuilder cmd = new StringBuilder();
        cmd.append("homesetparser");
        Log.d(TAG, "set ctl.start open "+cmd.toString());
       //
        WeChatUtils.exec(cmd.toString());

        mCurrWxUin = getCurrWxUin();
        mDbPassword = makeDbPassword(mPhoneIMEI, mCurrWxUin);

        // copyDbfromWx(mCurrWxUin);
        mSqlHelper = new SqlCipherHelper(mContext, DB_NAME, hook);
        long endTime = System.currentTimeMillis();
        long interval = endTime-startTime;
        Log.d(TAG, "open endTime:"+endTime+", interval:"+interval);
    }

    public void close(){
        if(mSqlHelper != null) {
            mSqlHelper.close();
            mSqlHelper = null;
        }
    }

    public List<WeChatInfo> selectConact() {
        List<WeChatInfo> weChatInfos = new ArrayList<WeChatInfo>();
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "selectConact startTime:"+ startTime);
        try {
            Log.d(TAG, "mDbPassword:"+mDbPassword);
            //打开数据库连接
            SQLiteDatabase db = mSqlHelper.getReadableDatabase(mDbPassword);
            //查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
            Cursor cursor = db.rawQuery("select r.username, r.alias, r.conRemark, r.nickname, i.reserved1 from rcontact r " +
                    "left join img_flag i on r.username = i.username " +
                    "where r.verifyFlag = 0 and r.type != 4 and r.type != 2 and r.type != 256 and r.type != 515 " +
                    "and r.type != 33 and r.type != 257 and r.nickname != '' limit 1, 5000;", null);

            while (cursor.moveToNext()) {
                String userName = cursor.getString(cursor.getColumnIndex("username"));
                String alias = cursor.getString(cursor.getColumnIndex("alias"));
                String conRemark = cursor.getString(cursor.getColumnIndex("conRemark"));
                String nickName = cursor.getString(cursor.getColumnIndex("nickname"));
                String iconUrl = cursor.getString(cursor.getColumnIndex("reserved1"));

                WeChatInfo weChatInfo = new WeChatInfo();
                weChatInfo.setUserName(userName);
                weChatInfo.setAlias(alias);
                weChatInfo.setConRemark(conRemark);
                weChatInfo.setNickName(nickName);
                weChatInfo.setIconUrl(iconUrl);

                weChatInfos.add(weChatInfo);

                Log.i(TAG, "userName:"+userName+" alias:"+alias+"conRemark:"+conRemark+" nickName:"+nickName);
                Log.i(TAG, "iconUrl:"+iconUrl);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "读取数据库信息失败" + e.toString());
            e.printStackTrace();
        }finally {

            long endTime = System.currentTimeMillis();
            long interval = endTime-startTime;
            Log.d(TAG, "selectConact endTime:"+endTime+", interval:"+interval);

            return weChatInfos;
        }

    }

    public void startVoip(String username){
        long currentTimeMillis = System.currentTimeMillis();
        Intent intent = new Intent();
        intent.setClassName("com.tencent.mm","com.tencent.mm.plugin.voip.ui.VideoActivity");
        intent.putExtra("Voip_User", username);
        intent.putExtra("Voip_Outcall", true);
        intent.putExtra("Voip_VideoCall", true);
        intent.putExtra("Voip_LastPage_Hash", currentTimeMillis);
        if (true) {
            intent.setFlags(603979776);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }

    public void startMutilVoip(){
        Intent intent = new Intent();
        intent.setClassName("com.tencent.mm", "com.tencent.mm.plugin.multitalk.ui.MultiTalkSelectContactUI");
        intent.putExtra("chatroomName", "7671027932@chatroom");
        intent.putExtra("key_need_gallery", true);
        intent.putExtra("titile", "创新");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);
    }

    /**
     * 获取微信的uid
     * 微信的uid存储在SharedPreferences里面
     * 存储位置\data\data\com.tencent.mm\shared_prefs\auth_info_key_prefs.xml
     */
    private String getCurrWxUin() throws WeChatException{
        File file = new File(WX_SP_UIN_PATH);
        try {
            FileInputStream in = new FileInputStream(file);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Element element : elements) {
                if ("_auth_uin".equals(element.attributeValue("name"))) {
                    return element.attributeValue("value");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "获取微信uid失败，请检查auth_info_key_prefs文件权限");
            throw new WeChatException("get wechat auth uin fail, please check auth_info_key_prefs is exist or not and check file 's permission");
        }

        return null;
    }

    private void copyDbfromWx(String uin) throws WeChatException{
        String cacheFoldeName = WeChatUtils.md5("mm"+uin);

        StringBuilder wxFileBuilder = new StringBuilder();
        wxFileBuilder.append(WX_DB_DIR_PATH);
        wxFileBuilder.append("/");
        wxFileBuilder.append(cacheFoldeName);
        wxFileBuilder.append("/");
        wxFileBuilder.append(DB_NAME);

        //1.chmod 777 EnMircoMsg.db for permission
        File source = new File(wxFileBuilder.toString());
        File dest = new File(mContext.getFilesDir()+"/"+DB_NAME);

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new WeChatException("FileNotFoundException, wechat db file not found");
        } catch (IOException e) {
            e.printStackTrace();
            throw new WeChatException(e.toString());
        } finally {
            try {
                if(inputChannel !=null) {
                    inputChannel.close();
                }
                if(outputChannel != null) {
                    outputChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 获取手机的imei码
     *
     * @return
     */
    private void initPhoneIMEI() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneIMEI = tm.getDeviceId();
        if(mPhoneIMEI == null){
            mPhoneIMEI = "1234567890ABCDEF";
        }else{
            Log.d(TAG, "mPhoneIMEI:"+mPhoneIMEI);
        }
    }

    /**
     * 根据imei和uin生成的md5码，获取数据库的密码（去前七位的小写字母）
     *
     * @param imei
     * @param uin
     * @return
     */
    private String makeDbPassword(String imei, String uin) {
        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uin)) {
            Log.d(TAG, "初始化数据库密码失败：imei或uid为空");
            return null;
        }
        String md5 = WeChatUtils.md5(imei + uin);
        String password = md5.substring(0, 7).toLowerCase();
        return password;
    }
}
