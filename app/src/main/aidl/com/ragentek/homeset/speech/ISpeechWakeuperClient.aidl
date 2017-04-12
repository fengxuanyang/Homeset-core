package com.ragentek.homeset.speech;
import com.ragentek.homeset.speech.IWakeuperListener;

interface ISpeechWakeuperClient {
    void startListening(IWakeuperListener listener);
    void stopListening();
}
