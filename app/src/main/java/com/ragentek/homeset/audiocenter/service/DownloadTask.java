package com.ragentek.homeset.audiocenter.service;


import com.ragentek.homeset.audiocenter.db.greendao.DownloadDBEntity;
import com.ragentek.homeset.audiocenter.utils.FileUtils;
import com.ragentek.homeset.audiocenter.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by xuanyang.feng on 2017/3/2.
 */

public class DownloadTask implements Runnable {
    private static final String TAG = "DownloadTask";
    private OkHttpClient mOkHttpClient;


    // taskid is the hashcode of url
    private Long taskId;
    private String url;
    private Long totalSize;
    private Long completedSize;
    private String fileName = url;    // File name when saving
    private String type;
    private String destFileDir;
    private RandomAccessFile accessFile;
    private File saveFile;
    private List<DownloadTaskListener> listeners;
    private String saveDirPath;
    private int UPDATE_SIZE = 1000 * 1024;    // The database is updated once every 50k

    private int downloadStatus = DowonloadState.DOWNLOAD_STATUS_PREPARE;


    public DownloadTask(Builder builder) {
        LogUtil.d(TAG, "DownloadTask");
        //  mContext = context.getApplicationContext();
        listeners = new ArrayList<>();
        init(builder);
    }

    public Long getTaskId() {
        return taskId;
    }

    private void init(Builder builder) {
        LogUtil.d(TAG, "init");

        fileName = builder.fileName;
        type = builder.type;
        saveDirPath = builder.saveDirPath;
        completedSize = builder.completedSize;
        totalSize = builder.totalSize;
        url = builder.url;
        taskId = builder.id;
        downloadStatus = builder.downloadStatus;

    }


    @Override
    public void run() {
        LogUtil.d(TAG, "run :" + Thread.currentThread().getName());

        saveFile = FileUtils.creatFile(saveDirPath, fileName);
        try {
            accessFile = new RandomAccessFile(saveFile, "rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (accessFile.length() < completedSize) {
                completedSize = accessFile.length();
            }
            accessFile.seek(completedSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "totalsize :" + totalSize + "completedSize :" + completedSize);

        downLoadFile(url);
    }


    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     */
    private void downLoadFile(String fileUrl) {
        LogUtil.d(TAG, ">>>>>>>>>>>>>>>>>downLoadFile");

        final Request request = new Request.Builder()
//                .header("RANGE", "bytes=" + completedSize + "-")//  Http value set breakpoints RANGE
//                .addHeader("Referer", url)
                .url(fileUrl)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
                         @Override
                         public void onFailure(Call call, IOException e) {
                             LogUtil.e(TAG, "  onFailure:" + e.toString());
                             onError(DownloadTaskListener.DOWNLOAD_NET_ERROR);
                         }

                         @Override
                         public void onResponse(Call call, Response response) throws IOException {
                             LogUtil.d(TAG, "onResponse downloadStatus:" + downloadStatus);

                             InputStream is = null;
                             byte[] buf = new byte[2048];
                             int len = 0;
                             FileOutputStream fos = null;
                             try {
                                 if (totalSize <= 0) {
                                     totalSize = response.body().contentLength();
                                 }
                                 LogUtil.d(TAG, "begin download total------>" + totalSize);
                                 switchState(DowonloadState.DOWNLOAD_STATUS_DOWNLOADING);
                                 is = response.body().byteStream();
                                 fos = new FileOutputStream(saveFile);
                                 int buffOffset = 0;

                                 while ((len = is.read(buf)) != -1) {
                                     buffOffset += len;
                                     completedSize += len;

                                     fos.write(buf, 0, len);
                                     if (buffOffset >= UPDATE_SIZE) {
                                         LogUtil.d(TAG, fileName + "--totalSize:" + totalSize + ",completedSize :" + completedSize);
                                         // Update download information
                                         buffOffset = 0;
                                         switchState(DowonloadState.DOWNLOAD_STATUS_DOWNLOADING);
                                     }
                                     if (downloadStatus == DowonloadState.DOWNLOAD_STATUS_PAUSE || downloadStatus == DowonloadState.DOWNLOAD_STATUS_CANCEL) {
                                         break;
                                     }
                                 }
                                 LogUtil.d(TAG, " download completed total------>" + totalSize);
                                 fos.flush();
                                 switchState(DowonloadState.DOWNLOAD_STATUS_COMPLETED);
                             } catch (IOException e) {
                                 switchState(DowonloadState.DOWNLOAD_STATUS_ERROR);
                                 LogUtil.d(TAG, e.toString());
                             } finally {
                                 try {
                                     if (is != null) {
                                         is.close();
                                     }
                                     if (fos != null) {
                                         fos.close();
                                     }
                                 } catch (IOException e) {
                                     switchState(DowonloadState.DOWNLOAD_STATUS_ERROR);
                                     LogUtil.e(TAG, e.toString());
                                 }
                             }
                         }

                     }

        );
        try {
            LogUtil.d(TAG, "execute");

            call.execute();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        LogUtil.d(TAG, "cancel");
        switchState(DowonloadState.DOWNLOAD_STATUS_CANCEL);

    }

    public void pause() {
        LogUtil.d(TAG, "cancel");
        switchState(DowonloadState.DOWNLOAD_STATUS_PAUSE);

    }

    public void resume() {
        LogUtil.d(TAG, "resume");
        switchState(DowonloadState.DOWNLOAD_STATUS_PREPARE);
    }

    private void switchState(int state) {
        synchronized (this) {
            downloadStatus = state;
        }
        switch (state) {
            case DowonloadState.DOWNLOAD_STATUS_DOWNLOADING:
                onDownloading();
                break;

            case DowonloadState.DOWNLOAD_STATUS_PREPARE:
                onPrepare();
                break;
            case DowonloadState.DOWNLOAD_STATUS_PAUSE:
                onPause();
                break;
            case DowonloadState.DOWNLOAD_STATUS_CANCEL:
                File temp = new File(saveDirPath + fileName);
                if (temp.exists()) temp.delete();
                onCancel();
                break;
            case DowonloadState.DOWNLOAD_STATUS_COMPLETED:
                onCompleted();
                break;

        }
    }

    private void onPrepare() {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onPrepare();
        }
    }


    private void onDownloading() {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onDownloading(totalSize, completedSize);
        }
    }

    private void onCompleted() {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onCompleted();
        }
    }

    private void onPause() {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onPause();
        }
    }

    private void onCancel() {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onCancel();
        }
    }

    private void onError(int errorCode) {
        if (listeners == null) {
            return;
        }
        for (DownloadTaskListener listener : listeners) {
            listener.onError(errorCode);
        }
    }

    public void addDownloadListener(DownloadTaskListener listener) {
        LogUtil.d(TAG, "addDownloadListener");
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeDownloadListener(DownloadTaskListener listener) {
        if (listener == null) {
            listeners.clear();
        } else {
            listeners.remove(listener);
        }
    }

    public void setmOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }


    /**
     * create the task through dbentity
     */
    public static class Builder {

        private String url;
        private String fileName = url;    // File name when saving
        private String type;
        private String saveDirPath;
        private Long id;
        private long totalSize;
        private long completedSize;         //  Download section has been completed
        private int downloadStatus = DowonloadState.DOWNLOAD_STATUS_PREPARE;
        private DownloadDBEntity dbEntity = null;

        public Builder setDBEntity(DownloadDBEntity dbEntity) {
            this.dbEntity = dbEntity;
            downloadStatus = dbEntity.getDownloadStatus();
            url = dbEntity.getUrl();
            id = dbEntity.getId();
            fileName = dbEntity.getFileName();
            type = dbEntity.getType();
            saveDirPath = dbEntity.getSaveDirPath();
            completedSize = dbEntity.getCompletedSize();
            totalSize = dbEntity.getToolSize();
            return this;
        }


        public DownloadTask build() {
            // id = (saveDirPath + fileName).hashCode() + "";
            return new DownloadTask(this);
        }
    }

    public interface DownloadTaskListener {

        int DOWNLOAD_ERROR_FILE_NOT_FOUND = -1;
        int DOWNLOAD_ERROR_IO_ERROR = -2;
        int DOWNLOAD_NET_ERROR = -3;

        void onPrepare();


        void onDownloading(long totalSize, long completedSize);

        void onPause();

        void onCancel();

        void onCompleted();

        void onError(int errorCode);


    }

    public static class DowonloadState {
        public static final int DOWNLOAD_STATUS_PREPARE = 0;
        public static final int DOWNLOAD_STATUS_DOWNLOADING = 2;
        public static final int DOWNLOAD_STATUS_CANCEL = 3;
        public static final int DOWNLOAD_STATUS_ERROR = 4;
        public static final int DOWNLOAD_STATUS_COMPLETED = 5;
        public static final int DOWNLOAD_STATUS_PAUSE = 6;
    }
}
