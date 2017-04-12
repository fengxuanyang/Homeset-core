package com.ragentek.homeset.audiocenter.utils;


/**
 * Created by xuanyang.feng on 2017/2/16.
 */

public class Constants {
    public static String[] CATEGORYS = {"音乐", "点播", "直播"};
    public static String APP_NAME = "audiocenter";
    public static String DB_NAME = "audiocenter_db";
    public static String ALBUM = "album";
    public static String CATEGORY = "category";
    public static String CATEGORY_TAG = "tag";
    public static String TYPE = "type";
    public static String PLAY_LIST = "playlist";
    public static String TASKEVENT_TYPE = "taskevent_type";
    public static String TASKEVENT_TYPE_SPEECH = "speech";

     public static String PLAY_LIST_ID = "listid";
    //    public static final int AUDIO_TYPE_TAG = 0; //album contains  album
    public static final int AUDIO_TYPE_ALBUM = 1;
    public static final int AUDIO_TYPE_MUSIC = 2; //  for migu
    public static final int AUDIO_TYPE_RADIO = 3;
    public static final int AUDIO_TYPE_SINGLE_MUSIC = 4; //only for fav music
    public static final int CATEGORY_FAV = -1;

    public static final int AUDIO_TYPE_ERROR = -1;

    //for playlist
    public static final int PLAYLIST_FAV = 0;
    //    public static final int PLAYLIST_TAG = 1;
    public static final int PLAYLIST_ALBUM = 2;
    public static final int PLAYLIST_RADIO = 3;
    public static final int PLAYLIST_MUSIC = 4;

//    public static final String ACTION_PLAY_FAV = "android.intent.action.playfav";
//    public static final String ACTION_PLAY_TAG = "android.intent.action.playtag";
//    public static final String ACTION_PLAY_RADIO = "android.intent.action.playradio";
//    public static final String ACTION_PLAY_MUSIC = "android.intent.action.playmusic";

    //group：收藏的是album还是music还是radio，music置group=0，album置group=1，radio置group=2【M】
    public static final int GROUP_MUSIC = 0;
    public static final int GROUP_ALBUM = 1;
    public static final int GROUP_RADIO = 2;
    public static final int UNFAV = 0;
    public static final int FAV = 1;


    public static final String DEFULT_CROSS_TALK = "郭德纲相声";


    public enum CATEGORYTAG {
        CROSS_TALK(12, "郭德纲相声"),
        CHINA_ART(16, "戏曲大全"),
        HEALTH(7, "中医也好玩"),
        STORYTELLING(12, "悬疑"),
        STOCK(8, "投资"),
        HISTORY(9, "春秋战国"),
        MUSIC(30000, "红歌");
         private int type;
        private String name;

        CATEGORYTAG(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum Radio {
        //        0, 1.国家台 2.省份台 3.网络台
        LOCAL(0, "本地台"),
        NATION(1, "国家台"),
        //        PROVINCE(2, "省份台"),
        NET(3, "网络台"),;

        private int type;
        private String name;

        Radio(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
