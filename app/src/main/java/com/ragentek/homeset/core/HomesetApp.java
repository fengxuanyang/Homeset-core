package com.ragentek.homeset.core;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.ragentek.homeset.audiocenter.service.MediaPlayerManager;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

public class HomesetApp extends MultiDexApplication {
    private static Context mContext;
    private static final String TAG = "HomesetApp";
    private final static String SHARED_PREFERENCES_NAME = "homeset_pref";
    private static SharedPreferences mPreferences;
    private static int MAX_MEM = (int) Runtime.getRuntime().maxMemory() / 4;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mPreferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        frescoInit();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        MediaPlayerManager.getInstance(this).init(new MediaPlayerManager.MediaManagerInitListener() {
            @Override
            public void onMediaManageInit(int result) {
                switch (result) {
                    case MediaPlayerManager.RESULT_INIT_COMPLETE:
                        LogUtil.e(TAG, "RESULT_INIT_COMPLETE");
                        break;
                    case MediaPlayerManager.RESULT_INIT_ERROE:
                        LogUtil.e(TAG, "RESULT_INIT_ERROE");
                        break;
                }
            }
        });
    }

    public static boolean isEnableDebugLog() {
        ContentResolver cr = mContext.getContentResolver();
        int value = Settings.System.getInt(cr, "robot_debug_log", 1);
        return (value == 1);
    }

    public static boolean isEnableEventLog() {
        ContentResolver cr = mContext.getContentResolver();
        int value = Settings.System.getInt(cr, "robot_event_log", 1);
        return (value == 1);
    }

    public static String getSettingString(String key, String defaultVal) {
        String ret = mPreferences.getString(key, defaultVal);
        return ret;
    }

    public static void setSettingString(String key, String val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, val);
        editor.commit();
    }


    public static Context getContext() {
        return mContext;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void frescoInit() {
        Fresco.initialize(this, getConfigureCaches(this));
    }

    private ImagePipelineConfig getConfigureCaches(Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEM,
                Integer.MAX_VALUE,
                MAX_MEM,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE / 10);

        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                .build();

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())
                .build();

        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig);
        return configBuilder.build();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();

    }
}
