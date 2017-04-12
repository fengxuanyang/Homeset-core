package com.ragentek.homeset.wechat;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by wenjin.wang on 2017/1/18.
 */

public class SqlCipherHelper extends SQLiteOpenHelper {

    public SqlCipherHelper(Context context, String db, SQLiteDatabaseHook hook){
        super(context, db, null, 1, hook);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
