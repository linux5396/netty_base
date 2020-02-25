package com.linxu.nio.bug;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author linxu
 * @date 2020/2/19
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class CPUFULL implements Serializable {
    private Object readResolve() {
        System.err.println("");
        return null;
    }

    static volatile boolean halfPackFlags = false;

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        System.out.println(selector.isOpen());
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);
        socketChannel.bind(inetSocketAddress);
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
        Set selectedKeys = selector.selectedKeys();

        for (; ; ) {
            System.out.println("等待...");
            /**
             * 通常是阻塞的，但是在epoll空轮询的bug中，
             * 之前处于连接状态突然被断开，select()的
             * 返回值noOfKeys应该等于0，也就是阻塞状态
             * 但是，在此bug中，select()被唤醒，而又
             * 没有数据传入，导致while (itr.hasNext())
             * 根本不会执行，而后就进入for (;;) {的死循环
             * 但是，正常状态下应该阻塞，也就是只输出一个waiting...
             * 而此时进入死循环，不断的输出waiting...，程序死循环
             * cpu自然很快飙升到100%状态。
             */
            int noOfKeys = selector.select();
            System.out.println("selected keys:" + noOfKeys);
            Iterator itr = selectedKeys.iterator();
            while (itr.hasNext()) {
                SelectionKey key = (SelectionKey) itr.next();
                if (key.isAcceptable()) {
                    SocketChannel client = socketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("The new connection is accepted from the client: " + client);
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    client.read(buffer);
                    String output = new String(buffer.array()).trim();
                    System.out.println("Message read from client: " + output);
                    if (output.equals("Bye Bye")) {
                        client.close();
                        System.out.println("The Client messages are complete; close the session.");
                    }
                    //注册监听读写操作位。
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    //通过半包标识，来触发写操作。
                } else if (key.isWritable()) {
                    System.err.println(key.channel() + "will write.");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    byteBuffer.put("server msg".getBytes());
                    try {
                        //取消写半包标志
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    channel.write(byteBuffer);
                    byteBuffer = null;
                }
                itr.remove();
            }
        }
    }
}
