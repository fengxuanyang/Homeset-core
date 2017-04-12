package com.ragentek.homeset.speech;

interface IRecognitionListener {
    void onBeginOfSpeech();
    void onEndOfSpeech();
    void onVolumeChanged(int value);
    void onSpeechEnd();
    void onRecordEnd();
    void onResult(String result);
    void onError(int errCode, String message);
}
