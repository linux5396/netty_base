package com.linxu.netty.samples.qps;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linxu
 * @date 2020/2/26
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class ConcurrentServerHandler extends ChannelInboundHandlerAdapter {
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private static ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int qps = atomicInteger.getAndSet(0);
            System.out.println("The QPS is:" + qps);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ((ByteBuf) msg).release();
        atomicInteger.incrementAndGet();
        //mock the bug which occurred in my Robots Testing.
        Random random = new Random();
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
    }
}
