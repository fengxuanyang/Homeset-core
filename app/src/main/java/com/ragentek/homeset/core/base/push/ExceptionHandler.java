package com.ragentek.homeset.core.base.push;


import com.ragentek.homeset.core.utils.LogUtils;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ExceptionHandler extends ChannelHandlerAdapter {
    private String mTag;

    public ExceptionHandler(String tag) {
        mTag = tag;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO: need to handle exceptions
        LogUtils.e(mTag, "exceptionCaught, case" + cause.toString());

        StackTraceElement[] stackElements = cause.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                LogUtils.e(mTag, "          " + stackElements[i]);
            }
        }
    }
}
