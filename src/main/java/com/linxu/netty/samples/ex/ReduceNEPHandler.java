package com.linxu.netty.samples.ex;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author linxu
 * @date 2020/3/2
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class ReduceNEPHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ReduceNEPHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(Arrays.toString(cause.getStackTrace()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //如果是由空转触发
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                //读空转超时

                ctx.channel().close();
            }
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                //写空转超时
            }
            //ctx.channel().close();
            //ctx.close();
        }
    }
}
