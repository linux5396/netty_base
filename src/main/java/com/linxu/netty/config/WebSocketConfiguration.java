package com.linxu.netty.config;

import com.linxu.netty.handler.WebSocketHandler;
import com.linxu.netty.handler.WebSocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author linxu
 * @date 2019/8/21
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
@Configuration
@PropertySource("classpath:/websocket.properties")
public class WebSocketConfiguration {
    @Value("${boss-count}")
    private int bossGroup;
    @Value("${worker-count}")
    private int workerGroup;
    @Value("${websocket.port}")
    private int port;
    @Value("${keep-Alive}")
    private boolean keepAlive;
    @Value("${back-log}")
    private int backlog;
    @Autowired
    @Qualifier("ws")
    private WebSocketInitializer initializer;

    @Bean(name = "webSocketServerBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                //启动Nagle算法，确保尽可能发送大一点的数据块
                .option(ChannelOption.TCP_NODELAY, true)
                //需要配合linux下的somaxconn文件设置
                .option(ChannelOption.SO_BACKLOG, 1024)
                //接受缓冲区大小
                .option(ChannelOption.SO_RCVBUF,1024)
                //发送缓冲区大小
                .option(ChannelOption.SO_SNDBUF,128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(initializer);
                    }
                });

        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();

        for (ChannelOption option : keySet) {
            bootstrap.option(option, tcpChannelOptions.get(option));
        }
        return bootstrap;
    }
    @Bean(name = "wsBossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(bossGroup);
    }

    @Bean(name = "wsWorkerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerGroup);
    }

    @Bean(name = "webSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(port);
    }

    @Bean(name = "wsChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>(2);
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        return options;
    }
}
