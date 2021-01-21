package com.github.remote.codec;

import com.github.remote.dto.ClientRequest;
import com.github.util.ProtobufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class ServerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        ClientRequest deserialize = ProtobufUtil.deserializer(bytes, ClientRequest.class);
        out.add(deserialize);
    }
}
