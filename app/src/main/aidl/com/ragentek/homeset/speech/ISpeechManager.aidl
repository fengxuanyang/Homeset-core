package com.ragentek.homeset.speech;

import com.ragentek.homeset.speech.ISpeechRecognitionClient;
import com.ragentek.homeset.speech.ISpeechSynthesizerClient;
import com.ragentek.homeset.speech.ISpeechWakeuperClient;

interface ISpeechManager {
    ISpeechRecognitionClient getRecognitionClient();
    ISpeechSynthesizerClient getSynthesizerClient();
    ISpeechWakeuperClient getWakeuperClient();
}
