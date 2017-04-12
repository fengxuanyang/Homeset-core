package com.ragentek.homeset.speech;

interface IWakeuperListener {
    void onResult(String result);
    void onError(int errorCode, String message);
}
