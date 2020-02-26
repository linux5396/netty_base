package com.linxu.netty.samples.qps;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author linxu
 * @date 2020/2/26
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class QPSClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        bootstrap.group(worker)
                //ban the nagle
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                }).channel(NioSocketChannel.class);
        try {
            bootstrap.connect("127.0.0.1", 8080).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {
        private ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //100 per sec
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                for (int i = 0; i < 100; i++) {
                    ByteBuf buf = Unpooled.buffer();
                    for (int j = 0; j < 128; j++) {
                        buf.writeByte((byte) i);
                    }
                    ctx.writeAndFlush(buf);
                }
                System.err.println("send over.");
            }, 0, 1000, TimeUnit.MILLISECONDS);
        }
    }
}
