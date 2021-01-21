package com.github.remote.server;

import com.github.annotation.RpcClient;
import com.github.zk.PathUtil;
import com.github.zk.ZookeeperFactory;
import com.github.remote.codec.ServerDecoder;
import com.github.remote.codec.ServerEncoder;
import com.github.remote.handler.ServerHandler;
import com.github.util.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class NettyServer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${rpc.port}")
    private int port=8888;
    @Autowired
    private ApplicationContext applicationContext;

    public static Map<String, Object> serviceMap = new HashMap<>();


    public void init() throws Exception {



        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ServerDecoder())
                                    .addLast(new ServerEncoder())
                                    .addLast(new IdleStateHandler(60L, 60L, 60L, TimeUnit.SECONDS))
                                    .addLast(new ServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress address = InetAddress.getLocalHost();
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(Constants.NETTY_SERVER + "/" + address.getHostAddress() + ":" + port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
        registService();
    }

    private void registService() throws Exception {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcClient.class);
        for (Object bean : beansWithAnnotation.values()) {
            Class<?> clazz = bean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfac : interfaces) {
                serviceMap.put(interfac.getName(), bean);
                InetAddress address = InetAddress.getLocalHost();
                String hostAddress = address.getHostAddress();
                String next = hostAddress + ":" + port;
                CuratorFramework client = ZookeeperFactory.create();
                String path = PathUtil.path(next);
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
        }
    }
}
