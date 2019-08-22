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
 */
@Component
@Slf4j
public class WebSocketServerStarter {
    private final ServerBootstrap serverBootstrap;

    private final InetSocketAddress webSocketPort;

    @Autowired
    public WebSocketServerStarter(@Qualifier("webSocketServerBootstrap") ServerBootstrap serverBootstrap,@Qualifier("webSocketAddress") InetSocketAddress webSocketPort) {
        this.serverBootstrap = serverBootstrap;
        this.webSocketPort = webSocketPort;
    }
    private ChannelFuture channelFuture;

    @PostConstruct
    public void start() throws InterruptedException {
        log.info("启动 web socket server ,监听端口为：" + webSocketPort);
        channelFuture = serverBootstrap.bind(webSocketPort).sync();
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        log.info("关闭 web socket server ");
        channelFuture.channel().closeFuture().sync();
    }
}
