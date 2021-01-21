package com.github.remote.handler;


import com.github.remote.client.CompleteFuture;
import com.github.remote.dto.ServerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Object result;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ServerResponse response = (ServerResponse) msg;
        CompleteFuture.setResponse(response);
    }

}
