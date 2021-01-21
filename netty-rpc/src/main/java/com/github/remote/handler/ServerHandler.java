package com.github.remote.handler;


import com.github.remote.server.Media;
import com.github.remote.dto.ClientRequest;
import com.github.remote.dto.ServerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private int lossCount;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            if (IdleState.READER_IDLE.equals(state)) {
                lossCount++;
                log.warn("{} 读空闲...一分钟后关闭...", ctx.channel().remoteAddress());
                if (lossCount > 1) {
                    log.warn("{}通道关闭...", ctx.channel().remoteAddress());
                    ctx.close();
                }
            } /*else if (event.equals(IdleState.WRITER_IDLE)) {

            } else {

            }*/
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        lossCount = 0;
        ClientRequest request = (ClientRequest) msg;
        Media media = Media.getInstance();
        Object response = media.process(request);
        if (response == null) {
            response = new ServerResponse();
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.error(cause.getMessage());
    }
}
