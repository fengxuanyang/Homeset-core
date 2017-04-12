package com.ragentek.homeset.audiocenter.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xuanyang.feng on 2017/2/22.
 */

public class AudioResult<T> implements Serializable {
    @SerializedName("res_code")
    private int resultCode;
    @SerializedName("res_msg")
    private T resultMessage;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(T resultMessage) {
        this.resultMessage = resultMessage;
    }
}
