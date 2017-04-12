package com.ragentek.homeset.speech;

import com.ragentek.homeset.speech.IRecognitionListener;
import android.content.Intent;

interface ISpeechRecognitionClient {
    void startRecognize(IRecognitionListener listener);
    void stopRecognize();
    void cancelRecognize();
}