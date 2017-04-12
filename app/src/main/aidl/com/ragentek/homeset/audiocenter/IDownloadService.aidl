// IDownloadService.aidl
package com.ragentek.homeset.audiocenter;

// Declare any non-default types here with import statements

interface IDownloadService {
    void startDowonloadFile(String name, String savapath, String url);
    void cancelDowonloadFile(String url);
    void pauseDowonloadFile(String url);
    void resumeDowonloadFile(String url);
}
