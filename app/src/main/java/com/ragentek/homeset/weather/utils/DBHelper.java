package com.ragentek.homeset.weather.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private static final String DATABASE_NAME = "etouch_ecalendar.db";
    private static final int DATABASE_VERSION = 1;

    private String mDatabasePath;

    private boolean mInCoping = false;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;

        String filePath = mContext.getFilesDir().getPath();
        mDatabasePath = filePath.replace("/files", "/databases/");
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        LogUtils.d(TAG, "createDataBase, dbExist=" + dbExist);

        if (!dbExist) {
            this.getReadableDatabase();
            try {
                if (!mInCoping) {
                    copyDataBase();
                }
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        String myPath = null;
        SQLiteDatabase checkDB = null;
        try {
            myPath = mDatabasePath + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            LogUtils.e(TAG, "checkDataBase, e=" + e.toString());
        }

        if (checkDB != null) {
            checkDB.close();
        }

        LogUtils.d(TAG, "checkDataBase, myPath=" + myPath + " checkDB=" + checkDB);

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        LogUtils.d(TAG, "copyDataBase");

        mInCoping = true;
        String outFileName = mDatabasePath + DATABASE_NAME;
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        mInCoping = false;
        LogUtils.d(TAG, "copyDataBase, complete");
    }

    public void openDataBase() throws SQLException {
        String myPath = mDatabasePath + DATABASE_NAME;
        LogUtils.d(TAG, "openDataBase, myPath=" + myPath);

        mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}