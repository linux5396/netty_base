package com.linxu.nio.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author linxu
 * @date 2020/2/17
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class AioServerHandler implements Runnable {
    private int port;
    CountDownLatch latch;
    AsynchronousServerSocketChannel channel;

    public AioServerHandler(int port) {
        this.port = port;
        try {
            channel = AsynchronousServerSocketChannel.open();
            channel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch=new CountDownLatch(1);
        doAccept();
        try {
            System.out.println("await.");
            latch.await();
            System.out.println("wake up.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void doAccept(){
        channel.accept(this,new AioAcceptCompletionHandler());
    }
}
