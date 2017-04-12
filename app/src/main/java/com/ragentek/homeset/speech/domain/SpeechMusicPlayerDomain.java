package com.ragentek.homeset.speech.domain;

public class SpeechMusicPlayerDomain extends SpeechBaseDomain {
    public  Semantic semantic = new Semantic();

    public static class Semantic {
        public Slots slots = new Slots();
    }

    public static class Slots {
        /**
         *  Before you use attrValue, you must check attrType is equal "String".
         *  attrType = String
         * */
        public String attrType = "";

        /** attrValue={上一首, 下一首, 停止, 暂停, 播放} */
        public String attrValue = "";
    }
}
