package com.linxu.netty.samples.qps;

import com.linxu.netty.AbstractStarter;
import com.sun.org.apache.bcel.internal.generic.NEW;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author linxu
 * @date 2020/2/26
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class ConcurrentServer extends AbstractStarter {
    /**
     * using bind handler thread pool
     * but, the bug say to me,this only call one thread.
     */
    final EventExecutorGroup EXEC = new DefaultEventExecutorGroup(100);

    @Override
    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup workers = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 1);
        bootstrap.group(boss, workers)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(EXEC, new ConcurrentServerHandler());
            }
        });
        try {
            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

    @Override
    public void stop() throws Exception {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        try {
            new ConcurrentServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
