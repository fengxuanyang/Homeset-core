package com.ragentek.homeset.wechat.domain;

/**
 * Created by wenjin.wang on 2017/3/29.
 */

public class WeChatException extends Exception {

    private String exception;

    public WeChatException(){
        super();
    }

    public WeChatException(String exception){
        exception = exception;
    }

    @Override
    public String toString() {
        return this.getClass() + " : " + exception;
    }
}
