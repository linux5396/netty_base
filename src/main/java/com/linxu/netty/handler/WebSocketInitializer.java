package com.linxu.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author linxu
 * @date 2019/8/21
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
@Component
@Qualifier("ws")
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //HttpObjectAggregator的作用：将一个HTTP请求的内容、头部等信息都聚合成一个FullHttpRequest或者FullHttpResponse.
        //HttpServerCodec本质上是一个requestDecoder和一个responseEncoder.主要是用于编码解码
        channel.pipeline()
                .addLast(new DefaultEventLoopGroup(8),
                        new HttpServerCodec(),
                        new HttpObjectAggregator(65536),
                        new ChunkedWriteHandler(),
                        new IdleStateHandler(60, 0, 0,TimeUnit.SECONDS),
                        webSocketHandler
                );
    }
}
