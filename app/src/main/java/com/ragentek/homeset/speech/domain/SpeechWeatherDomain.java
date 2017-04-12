package com.ragentek.homeset.speech.domain;

import java.util.ArrayList;

public class SpeechWeatherDomain extends SpeechBaseDomain {
    public static final String CURRENT_CITY = "CURRENT_CITY";
    public static final String CURRENT_DAY = "CURRENT_DAY";

    public Semantic semantic = new Semantic();
    public Data data = new Data();

    public static class Semantic {
        public Slots slots = new Slots();
    }

    public static class Slots {
        public Location location = new Location();
        public DateTime datetime = new DateTime();
    }

    public static class Location {
        public String country = "";
        public String province = "";
        public String city = "";
        public String type = "";
        public String keyword = "";
        public String cityAddr = "";
    }

    public static class DateTime {
        public String date = "";
        public String type = "";
        public String dateOrig = "";
        public String time = "";
        public String timeOrig = "";
    }

    public static class Data {
        public ArrayList<Result> result = new ArrayList<Result>();
    }

    public static class Result {
        public String city = "";
        public String date = "";
        public String weather = "";
        public String tempRange = "";
        public String wind = "";
        public String airQuality = "";
        public String humidity = "";
    }
}
