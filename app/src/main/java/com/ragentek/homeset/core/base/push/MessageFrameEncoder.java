/*******************************************************************************
 * Copyright (c) 2016 Xiaodi Information Technologies Co. Ltd.
 * All rights reserved
 *******************************************************************************/
package com.ragentek.homeset.core.base.push;

import com.ragentek.protocol.messages.tcp.PushMessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MessageFrameEncoder extends MessageToByteEncoder<PushMessagePack> {
    public static final byte[] MSG_SUFFIX_BYTES = new byte[] { '\r', '\n' };

    // outbound data packet encoding, all incoming messages are from Redis
    @Override
    protected void encode(ChannelHandlerContext ctx, PushMessagePack mp, ByteBuf out)
            throws Exception {
        String msg = mp.toJSONString();
//         byte[] msg_bytes = msg.getBytes(CharsetUtil.UTF_8);

        byte[] msg_bytes = msg.getBytes(CharsetUtil.UTF_8); //TODO: check encoding UTF-8
        int msg_length = msg_bytes.length;
        out.writeBytes(MSG_SUFFIX_BYTES);
        out.writeInt(msg_length);
        out.writeBytes(msg_bytes);
    }
}
