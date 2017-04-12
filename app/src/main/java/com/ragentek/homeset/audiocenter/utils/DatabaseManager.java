package com.ragentek.homeset.audiocenter.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ragentek.homeset.audiocenter.db.greendao.CollectAlbumDao;
import com.ragentek.homeset.audiocenter.db.greendao.CollectMusicDao;
import com.ragentek.homeset.audiocenter.db.greendao.DaoMaster;
import com.ragentek.homeset.audiocenter.db.greendao.DaoSession;
import com.ragentek.homeset.audiocenter.db.greendao.DownloadDBEntityDao;

/**
 * Created by xuanyang.feng on 2017/3/1.
 */

public class DatabaseManager {
    private static DatabaseManager mDatabaseManager;
    private DaoSession mDaoSession;

    private DatabaseManager(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, Constants.DB_NAME, null);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        DaoMaster master = new DaoMaster(database);
        mDaoSession = master.newSession();
    }

    public static DatabaseManager getInstance(Context mcontext) {
        if (mDatabaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (mDatabaseManager == null) {
                    mDatabaseManager = new DatabaseManager(mcontext.getApplicationContext());
                }
            }
        }
        return mDatabaseManager;
    }


    public CollectMusicDao getCollectMusicDao() {
        return mDaoSession.getCollectMusicDao();
    }

    public CollectAlbumDao getCollectAlbumDao() {
        return mDaoSession.getCollectAlbumDao();
    }

    public DownloadDBEntityDao getDownloadDBEntityDao() {
        return mDaoSession.getDownloadDBEntityDao();
    }

}
