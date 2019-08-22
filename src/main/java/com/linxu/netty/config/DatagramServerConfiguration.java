package com.linxu.netty.config;

import com.linxu.netty.handler.DatagramHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author linxu
 * @date 2019/8/22
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
@Configuration
@PropertySource("classpath:/datagram.properties")
public class DatagramServerConfiguration {
    @Value("${udp.port}")
    private int port;
    @Autowired
    private DatagramHandler handler;

    @Bean("udpBootstrap")
    public Bootstrap bootstrap() {
        EventLoopGroup group = new NioEventLoopGroup(2);
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(handler);
        return b;
    }

    @Bean("udpAddress")
    public InetSocketAddress udpPort() {
        return new InetSocketAddress(port);
    }
}
