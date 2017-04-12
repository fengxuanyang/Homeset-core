package com.ragentek.homeset.weather.handler;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.ragentek.homeset.weather.data.AlmanacData;
import com.ragentek.homeset.weather.utils.DBHelper;
import com.ragentek.homeset.weather.utils.LogUtils;

import java.io.IOException;


public class AlmanacHandler {
    private static final String TAG = "AlmanacHandler";

    private Context mContext;

    public interface AlmanacHandlerListener {
        void onGetAlmanacResponse(AlmanacData data);
    }

    AlmanacHandlerListener mListener;

    public AlmanacHandler(Context context, AlmanacHandlerListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void almanac(String date) {
        AlmanacThread almanacThread = new AlmanacThread(mContext, mListener, date);
        almanacThread.start();
    }

    private class AlmanacThread extends Thread {
        Context context;
        AlmanacHandlerListener listener;
        String date;

        public AlmanacThread(Context context, AlmanacHandlerListener listener, String date) {
            this.context = context;
            this.listener = listener;
            this.date = date;
        }

        @Override
        public void run() {
            LogUtils.d(TAG, "AlmanacThread, date=" + date);

            DBHelper dbHelper = new DBHelper(context);
            try {
                dbHelper.createDataBase();
            } catch (IOException e) {
                LogUtils.e(TAG, "AlmanacThread, create db exception, e=" + e.toString());
            }

            try {
                dbHelper.openDataBase();
            } catch (SQLException e) {
                LogUtils.e(TAG, "AlmanacThread, open db exception, e=" + e.toString());
            }

            AlmanacData almanacData = null;
            String sql = "SELECT * FROM huangli WHERE date='" + date + "'";
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String yi = cursor.getString(cursor.getColumnIndex("yi"));
                    String ji = cursor.getString(cursor.getColumnIndex("ji"));

                    almanacData = new AlmanacData();
                    almanacData.setYi(yi);
                    almanacData.setJi(ji);
                }
                cursor.close();
            }
            dbHelper.close();

            LogUtils.d(TAG, "AlmanacThread, call listener=" + listener);
            if (listener != null) {
                listener.onGetAlmanacResponse(almanacData);
            }
        }
    }
}