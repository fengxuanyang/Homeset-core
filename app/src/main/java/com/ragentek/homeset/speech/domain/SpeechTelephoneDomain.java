package com.ragentek.homeset.speech.domain;

public class SpeechTelephoneDomain extends SpeechBaseDomain {
    public Semantic semantic = new Semantic();

    public static class Semantic {
        public Slots slots = new Slots();
    }

    public static class Slots {
        /** contact name*/
        public String name = "";

        /** phone number */
        public String code = "";

        public String teleOperator = "";
        public String category = "";
    }
}
