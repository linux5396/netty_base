package com.linxu.netty.config;

import com.linxu.netty.handler.SocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@PropertySource("classpath:/socket.properties")
@Slf4j
public class SocketServerConfiguration {
    @Value("${boss.count}")
    private int bossCount;

    @Value("${worker.count}")
    private int workerCount;

    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${keep.alive}")
    private boolean keepAlive;

    @Value("${backlog}")
    private int backLog;

    @Autowired
    private SocketInitializer initializer;

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(initializer);

        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();

        for (ChannelOption option : keySet) {
            bootstrap.option(option, tcpChannelOptions.get(option));
        }
        return bootstrap;
    }
    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<>(2);
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backLog);
        return options;
    }

    @Bean(name = "stringEncoder")
    public StringEncoder stringEncoder() {
        return new StringEncoder();
    }

    @Bean(name = "stringDecoder")
    public StringDecoder stringDecoder() {
        return new StringDecoder();
    }
}
