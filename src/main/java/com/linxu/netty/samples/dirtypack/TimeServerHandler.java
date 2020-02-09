package com.linxu.netty.samples.dirtypack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    int count=0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");*/
        System.out.println("time server rec command: " + msg+" count is:"+ ++count);
        String curTime = "QUERY TIME".equalsIgnoreCase((String)msg) ? new Date(System.currentTimeMillis()).toString()+System.getProperty("line.separator") : "BAD Command."+System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(curTime.getBytes());
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
