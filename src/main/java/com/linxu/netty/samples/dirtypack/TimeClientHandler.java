package com.linxu.netty.samples.dirtypack;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private final String delimeter = System.getProperty("line.separator");
    //添加换行符，解决String拆包问题
    private final String queryTime = "QUERY TIME" + delimeter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(queryTime.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("resp: " + msg);
    }
}
