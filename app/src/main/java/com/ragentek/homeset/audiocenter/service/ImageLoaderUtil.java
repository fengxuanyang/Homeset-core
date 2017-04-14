package com.ragentek.homeset.audiocenter.service;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * Created by xuanyang.feng on 2017/4/14.
 */


public class ImageLoaderUtil {
    private static final String TAG = "ImageLoaderUtil";
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private static final int MAX_CACHE_MEMORY_SIZE = MAX_HEAP_SIZE / 4;
    private static final int MAX_CACHE_DISK_SIZE = 50 * 1024 * 1024;
    private Context mContext;
    private static ImageLoaderUtil mImageLoaderManager;
    private GlideRequestListener mGlideRequestListener = new GlideRequestListener();

    private ImageLoaderUtil(Context context) {
        mContext = context;
    }


    public static ImageLoaderUtil getInstance(Context context) {
        if (mImageLoaderManager == null) {
            synchronized (ImageLoaderUtil.class) {
                if (mImageLoaderManager == null) {
                    mImageLoaderManager = new ImageLoaderUtil(context);
                }
            }
        }
        return mImageLoaderManager;
    }

    public void displayImage(ImageView imageView, Uri path, int defaultresuorceid, int errorid) {
        Glide.with(mContext)
                .load(path)
                .placeholder(defaultresuorceid)
                .error(errorid)
                .listener(mGlideRequestListener)
                .into(imageView);

    }

    public void displayImage(ImageView imageView, Uri path, int errorid) {
        Glide.with(mContext)
                .load(path)
                .error(errorid)
                .listener(mGlideRequestListener)
                .into(imageView);
    }

    public void displayImage(ImageView imageView, Uri path) {
        Glide.with(mContext)
                .load(path)
                .listener(mGlideRequestListener)
                .into(imageView);
    }

    public void release() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(mContext).clearDiskCache();
                Glide.get(mContext).clearMemory();
            }
        }).start();

    }

    public class GlideConfigModule implements GlideModule {

        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, MAX_CACHE_DISK_SIZE));
            builder.setMemoryCache(new LruResourceCache(MAX_CACHE_MEMORY_SIZE));
            builder.setBitmapPool(new LruBitmapPool(MAX_CACHE_MEMORY_SIZE));
        }

        @Override
        public void registerComponents(Context context, Glide glide) {
        }
    }

    public class GlideRequestListener implements RequestListener {

        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            LogUtil.e(TAG, "onException: " + e);
            LogUtil.e(TAG, "onException: " + model);
            LogUtil.e(TAG, "onException: " + target.getRequest().isRunning());
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            LogUtil.d(TAG, "onResourceReady:  model: " + model + ",isFromMemoryCache" + isFromMemoryCache + ",isFirstResource" + isFirstResource);
            return false;
        }
    }
}
