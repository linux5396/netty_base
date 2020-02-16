package com.linxu.nio.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author linxu
 * @date 2020/2/17
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class AioAcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel result, AioServerHandler attachment) {
        //注意这个地方，重复调用accept,保证继续接入新的客户端；
        attachment.channel.accept(attachment, this);
        ByteBuffer recv = ByteBuffer.allocateDirect(1024);
        //当IO执行完毕，会自动调用后继的处理器。
        result.read(recv, recv, new AioServerChannelHandler(result));

    }

    @Override
    public void failed(Throwable exc, AioServerHandler attachment) {
        exc.printStackTrace();
        System.err.println("accept not complete!");
    }
}
