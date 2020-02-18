package com.linxu.buffer.usage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author linxu
 * @date 2020/2/18
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class ZeroCopyFileChannel extends ChannelInboundHandlerAdapter {
    /**
     * 如果没有这个，我们拷贝数据的时候，将读取到堆内缓冲区，再写回文件；
     * 通过fileChannel,我们可以直接用文件通道进行传输；
     * 节省了堆内内存的使用；
     *
     * @param src
     * @param dest
     * @throws Exception
     */
    public static void copyFileWithFileChannel(String src, String dest) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(src, "r");
        FileChannel fileChannel = raf.getChannel();
        RandomAccessFile destf = new RandomAccessFile(dest, "rw");
        FileChannel destChannel = destf.getChannel();
        long posi = 0;
        long size = fileChannel.size();
        fileChannel.transferTo(posi, size, destChannel);
    }

    /**
     * 通过default file region传输；底层是 NIO fileChannel实现。来传输文件；
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RandomAccessFile raf = null;
        long length = -1;
        try {
            // 1. 通过 RandomAccessFile 打开一个文件.
            raf = new RandomAccessFile((String) msg, "r");
            length = raf.length();
        } catch (Exception e) {
            ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n');
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }

        ctx.write("OK: " + raf.length() + '\n');
        if (ctx.pipeline().get(SslHandler.class) == null) {
            // SSL not enabled - can use zero-copy file transfer.
            // 2. 调用 raf.getChannel() 获取一个 FileChannel.
            // 3. 将 FileChannel 封装成一个 DefaultFileRegion
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length));
        } else {
            // SSL enabled - cannot use zero-copy file transfer.
            ctx.write(new ChunkedFile(raf));
        }
        ctx.writeAndFlush("\n");
    }
}
