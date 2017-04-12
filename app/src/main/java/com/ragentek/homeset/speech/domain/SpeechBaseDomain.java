package com.ragentek.homeset.speech.domain;


import com.alibaba.fastjson.JSON;

public class SpeechBaseDomain {
    public String text = "";
    public String service = "";
    public int rc;
    public String operation = "";

    @Override
    public String toString() {
        String result = "[domain=" + SpeechDomainUtils.getDomainType(this).name() + ", json=" + JSON.toJSONString(this) + "]";
        return result;
    }
}
