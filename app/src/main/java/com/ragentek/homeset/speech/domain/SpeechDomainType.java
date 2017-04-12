package com.ragentek.homeset.speech.domain;

public enum SpeechDomainType {
    NULL(0, null, null, null),
    MUSIC(1, "music", null, "我要听音乐; 我要听歌"),
    MUSIC_PLAYER(2, "musicPlayer_smartHome", null, "上一首; 下一首; 播放; 暂停"),
    TELEPHONE(3, "telephone", null, "我要打电话"),
    WEATHER(4, "weather", null, "今天的天气"),

    /** OpenQA type, which has subtype */
    OPENQA(100, "openQA", null, null),
    HOMESET_FAVORITE(101, "openQA", "homeset_favorite", "我的最爱; 我喜欢的; 播放我的最爱; 播放我喜欢的"),
    HOMESET_CROSSTALK(102, "openQA", "homeset_crosstalk", "我要听相声"),
    HOMESET_OPERA(103, "openQA", "homeset_opera", "我要听曲艺; 我要听戏曲"),
    HOMESET_STROY(104, "openQA", "homeset_story", "我要听书; 我要听故事"),
    HOMESET_HEALTH(105, "openQA", "homeset_health", "我要听健康"),
    HOMESET_FINACE(106, "openQA", "homeset_finance", "我要听财经"),
    HOMESET_HISTORY(107, "openQA", "homeset_history", "我要听历史"),
    HOMESET_RADIO(108, "openQA", "homeset_radio", "我要听广播; 我要听FM; 我要听收音机");

    private int code;
    private final String type;
    private final String subType;
    private final String description;

    SpeechDomainType(int code, String type, String subType, String description) {
        this.code = code;
        this.type = type;
        this.subType = subType;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getAllType() {
        return type + subType;
    }
}
