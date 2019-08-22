package com.linxu.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author linxu
 * @date 2019/8/21
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
@Component
public class SocketInitializer extends ChannelInitializer<SocketChannel> {
    @Resource
    private StringDecoder decoder;
    @Resource
    private StringEncoder encoder;

    private final SocketHandler handler;

    @Autowired
    public SocketInitializer(SocketHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        //这里的空转处理器必须放在入站的队列头：
        //因为需要计算时间差，如果该handler放在后面，就无法获取得到channel的读、写情况
        //读空转指多久时间没接收到客户端消息
        //写空转指多久时间没有向客户端发送消息
        //所有空闲时间
        //如果有操作时间超过，就会触发UserEventTriggered事件
        //我们只需要在我们的handler中重写userE...进行处理即可
        //0表示禁用
        channel.pipeline()
                .addLast("idleStateHandler", new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(handler);
    }
}
