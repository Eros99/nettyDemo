package com.github.remote.codec;

import com.github.util.ProtobufUtil;
import com.github.remote.dto.ServerResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class ClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        ServerResponse deserialize = ProtobufUtil.deserializer(bytes, ServerResponse.class);
        out.add(deserialize);
    }
}
