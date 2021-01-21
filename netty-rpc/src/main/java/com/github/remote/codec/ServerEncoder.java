package com.github.remote.codec;

import com.github.util.ProtobufUtil;
import com.github.remote.dto.ServerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class ServerEncoder extends MessageToByteEncoder<ServerResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ServerResponse msg, ByteBuf out) throws Exception {
        out.writeBytes(ProtobufUtil.serializer(msg));
    }
}
