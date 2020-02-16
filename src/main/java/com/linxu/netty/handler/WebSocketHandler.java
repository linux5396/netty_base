package com.linxu.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author linxu
 * @date 2019/8/21
 * <tip>take care of yourself.everything is no in vain.</tip>
 * web socket的处理是由HTTP升级的，因此要做好充分的处理
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            log.info("启用HTTP连接");
            //传统HTTP接入
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            return;
        }
        if (msg instanceof WebSocketFrame) {
            log.info("启用 web socket连接");
            //websocket接入
            handlerWebSocket(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        //先验证头部


        //下面是验证版本等
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:9090/websocket", null, false);
        //这里会判断websocket的协议版本等；如果创建失败，则证明握手失败
        handshaker = handshakerFactory.newHandshaker(request);

        if (handshaker == null) {
            log.warn("该HTTP不支持升级为web socket.");
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            // 动态加入web socket的编解码处理
            try {
                //加入的过程在源码中
                handshaker.handshake(ctx.channel(), request);
            } catch (Exception e) {
                log.error("web socket handshake occurred error.Missing upgrade! ");
            }
            //这里就可以存储连接了！
            //TODO
        }
    }

    private void handlerWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //如果是关闭帧
        if (frame instanceof CloseWebSocketFrame) {
            log.warn("received closed frame，{}",frame.content().retain());
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否Ping消息
        if (frame instanceof PingWebSocketFrame) {
            log.info("ping message:{}", frame.content().retain());
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 判断是否Pong消息
        if (frame instanceof PongWebSocketFrame) {
            log.info("pong message:{}", frame.content().retain());
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //文本信息
        if (frame instanceof TextWebSocketFrame) {
            log.info("正常文本消息.");
            //do some task：对数据进行验证，非法数据直接丢弃；正确数据可以传送给下一个handler
            System.err.println(((TextWebSocketFrame) frame).text());
            //将数据传入给下一个handler进行处理
           // ctx.fireChannelRead(frame);
        } else if (frame instanceof BinaryWebSocketFrame) {
            //二进制消息，如图像等
            throw new UnsupportedOperationException("不支持非文本数据");
        }

    }
}
