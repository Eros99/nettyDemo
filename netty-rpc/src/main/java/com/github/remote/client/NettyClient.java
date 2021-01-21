package com.github.remote.client;

import com.github.remote.codec.ClientDecoder;
import com.github.remote.codec.ClientEncoder;
import com.github.remote.handler.ClientHandler;
import com.github.remote.dto.ClientRequest;
import com.github.remote.dto.ServerResponse;
import com.github.util.Constants;
import com.github.zk.ZookeeperFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;


public class NettyClient {
    private ExecutorService executorService = new ThreadPoolExecutor(5, 10,
                        1L, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    @Value("${rpc.port}")
    private int port=8888;

    static Set<String> realPaths = new HashSet<>();

    private ChannelFuture channelFuture;

    public ServerResponse send(ClientRequest request) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ClientDecoder())
                                    .addLast(new ClientEncoder())
                                    .addLast(new IdleStateHandler(60L, 60L, 60L, TimeUnit.SECONDS))
                                    .addLast(new ClientHandler());
                        }
                    });
            CuratorFramework curator = ZookeeperFactory.create();
            List<String> paths = curator.getChildren().forPath(Constants.NETTY_SERVER);

            Watcher watcher = new ServerWatcher();
            curator.getChildren().usingWatcher(watcher).forPath(Constants.NETTY_SERVER);
            String host = "localhost";
            for (String path : paths) {
                realPaths.add(path);
            }
            if (realPaths.size() > 0) {
                String[] path = realPaths.toArray()[0].toString().split(":");
                host = path[0];
                port = Integer.parseInt(path[1]);
            }
            channelFuture = bootstrap.connect(host, port).sync();

            channelFuture.channel().writeAndFlush(request);
            CompleteFuture future = new CompleteFuture(request);
            ServerResponse serverResponse = executorService.submit(future).get(5, TimeUnit.SECONDS);
            channelFuture.channel().closeFuture();
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            group.shutdownGracefully();
        }
    }
}
