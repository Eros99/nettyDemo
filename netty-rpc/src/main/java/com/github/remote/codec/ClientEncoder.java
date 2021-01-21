package com.github.remote.codec;


import com.github.remote.dto.ClientRequest;
import com.github.util.ProtobufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientEncoder extends MessageToByteEncoder<ClientRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ClientRequest msg, ByteBuf out) throws Exception {
        byte[] bytes = ProtobufUtil.serializer(msg);
        out.writeBytes(bytes);
    }
}
