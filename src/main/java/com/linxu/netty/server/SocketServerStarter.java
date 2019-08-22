package com.linxu.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @author linxu
 * @date 2019/8/21
 * <tip>take care of yourself.everything is no in vain.</tip>
 * socket 服务器的启动组件
 */
@Component
@Slf4j
public class SocketServerStarter {
    private final ServerBootstrap serverBootstrap;

    private final InetSocketAddress tcpPort;

    @Autowired
    public SocketServerStarter(@Qualifier("serverBootstrap") ServerBootstrap serverBootstrap, @Qualifier("tcpSocketAddress") InetSocketAddress tcpPort) {
        this.serverBootstrap = serverBootstrap;
        this.tcpPort = tcpPort;
    }

    private ChannelFuture channelFuture;

    @PostConstruct
    public void start() throws InterruptedException {
        log.info("启动 socket server ,监听端口为：" + tcpPort);
        channelFuture = serverBootstrap.bind(tcpPort).sync();
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        log.info("关闭 socket server ");
        channelFuture.channel().closeFuture().sync();
    }
}
