package com.ragentek.homeset.core.base.push;

import com.alibaba.fastjson.JSON;
import com.ragentek.protocol.messages.tcp.PushMessagePack;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MessageFrameDecoder extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        PushMessagePack messagePack = JSON.parseObject(msg, PushMessagePack.class);
        out.add(messagePack);
    }
}
