package com.linxu.netty.samples.dirtypack;

import com.linxu.netty.samples.endecode.MessagePackDecoder;
import com.linxu.netty.samples.endecode.MessagePackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class TimeClient {
    public void connect(String host, int port) {
        EventLoopGroup eventExecutors = new NioEventLoopGroup(1);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(eventExecutors)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        //自定义的编码器
                         //new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$_".getBytes()));
                       // new FixedLengthFrameDecoder(10);
//                         channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                        channel.pipeline().addLast(new StringDecoder());
//                        channel.pipeline().addLast(new TimeClientHandler());
                        //channel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                        //添加两个字节的帧头部，解决粘包/半包问题
                        channel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                        channel.pipeline().addLast("msg en", new MessagePackEncoder());
                       // channel.pipeline().addLast("msg de", new MessagePackDecoder());
                        channel.pipeline().addLast(new SelfObjClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new TimeClient().connect("127.0.0.1", 8080);
    }
}
