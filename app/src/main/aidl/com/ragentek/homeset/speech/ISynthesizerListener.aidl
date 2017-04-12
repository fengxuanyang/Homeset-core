package com.ragentek.homeset.speech;

interface ISynthesizerListener {
    void onSpeakBegin();
    void onSpeakProgress(int percent, int beginPos, int endPos);
    void onCompleted(int errorCode, String message);
}