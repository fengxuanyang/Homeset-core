package com.ragentek.homeset.speech.domain;


public class SpeechHomesetDomain extends SpeechBaseDomain {
    public Answer answer = new Answer();

    public static class Answer {
        public String type = "";
        public String text = "";
    }
}
