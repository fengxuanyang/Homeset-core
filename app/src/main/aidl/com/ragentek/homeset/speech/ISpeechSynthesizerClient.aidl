package com.ragentek.homeset.speech;
import com.ragentek.homeset.speech.ISynthesizerListener;
import android.content.Intent;

interface ISpeechSynthesizerClient {
    void startSpeak(String text,  ISynthesizerListener listener);
    void stopSpeak();
    void pauseSpeak();
    void resumeSpeak();
    boolean isSpeaking();
}