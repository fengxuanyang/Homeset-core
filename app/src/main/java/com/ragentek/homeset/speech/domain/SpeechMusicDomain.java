package com.ragentek.homeset.speech.domain;

import java.util.ArrayList;

public class SpeechMusicDomain extends SpeechBaseDomain {
    public  Semantic semantic = new Semantic();
    public Data data = new Data();

    public static class Semantic {
        public Slots slots = new Slots();
    }

    public static class Slots {
        public String song = "";
        public String artist = "";
        public String album = "";
        public String category = "";
    }

    public static class Data {
        public ArrayList<Result> result = new ArrayList<Result>();
    }

    public static class Result {
        public String singer = "";
        public String name = "";
        public String downloadUrl = "";
        public String sourceName = "";
    }

}
