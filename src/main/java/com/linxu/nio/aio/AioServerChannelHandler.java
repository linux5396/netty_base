package com.linxu.nio.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.Date;

/**
 * @author linxu
 * @date 2020/2/17
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class AioServerChannelHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public AioServerChannelHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body, "UTF-8");
            // System.out.println("recv:" + req);
            String resp = new Date(System.currentTimeMillis()).toString();
            doWrite(resp);
            //实现再次接收的功能
            ByteBuffer recv = ByteBuffer.allocateDirect(128);
            channel.read(recv, recv, this);
            recv = null;
            resp = null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String resp) {
        byte[] bytes = resp.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        try {
            channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) {
                        //递归发送，UNTIL发送完毕
                        channel.write(attachment, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //ignore remaining.
                }
            });
        } catch (WritePendingException e) {
            System.err.println("happen wri a ex. re write");
            channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) {
                        //递归发送，UNTIL发送完毕
                        channel.write(attachment, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //ignore remaining.
                }
            });
        }
    }
}
