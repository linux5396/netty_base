package com.linxu.netty.samples.dirtypack;

import com.linxu.netty.samples.endecode.MessagePackDecoder;
import com.linxu.netty.samples.endecode.MessagePackEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class TimeServer {
    public void bind(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 256)
                .childHandler(new ChildHandler());
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    private class ChildHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
//            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
//            channel.pipeline().addLast(new StringDecoder());
//            channel.pipeline().addLast(new TimeServerHandler());
            channel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
          //  channel.pipeline().addLast("msg en", new MessagePackDecoder());
           // channel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
            channel.pipeline().addLast("msg de", new MessagePackDecoder());
            channel.pipeline().addLast(new SelfObjServerHandler());
        }
    }

    public static void main(String[] args) {
        new TimeServer().bind(8080);
    }
}
