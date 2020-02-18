package com.linxu.buffer.usage;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * @author linxu
 * @date 2020/2/18
 * <tip>take care of yourself.everything is no in vain.</tip>
 */
public class CompositeBufferUsage {
    /**
     * 就是说呢，CompositeBuffer可以把多个具有数据的buffer组合；
     * 通常来说，比如一个报文分为头部，报文体；
     * 头部的readidx=0 wriidx=20
     * body的readidx=0 wriidx=64
     * 则组合后的buffer的readidx=0 wriidx=84
     * 使用Component[]数组拷贝Buffer的引用，实现组合数据的0拷贝。
     */
    public static void main(String[] args) throws Exception {
        //CompositeByteBuf compositeByteBuf = new CompositeByteBuf(new UnpooledByteBufAllocator(true), true, 16);
        CompositeByteBuf compositeByteBuf1 = Unpooled.compositeBuffer(16);
        //read write idx的用处；readable=wridx-readidx.
        ByteBuf byteBuf = Unpooled.buffer(64);
        System.out.println(byteBuf.readerIndex());
        byteBuf.writeBytes("i am linxu".getBytes());
        byte[] read = new byte[byteBuf.readableBytes()];
        // read[0] = byteBuf.readByte();
        byteBuf.readBytes(read);
        System.out.println(new String(read, "UTF-8"));
        System.out.println(byteBuf.readerIndex());
        System.out.println("rare readable:" + byteBuf.readableBytes());
        byteBuf.writeBytes("i am linxu".getBytes());
        System.out.println("wri idx：" + byteBuf.writerIndex());
        //剩余的10会直接进入组合 buffer
        System.out.println("rare readable:" + byteBuf.readableBytes());
        //使用comppsiteByteBuf
        compositeByteBuf1.addComponent(true,byteBuf);
        compositeByteBuf1.writeBytes("123456789123456789123456789123456789".getBytes());
        System.out.println("composite buffer readable:" + compositeByteBuf1.readableBytes());
        //
        System.out.println("********");
        ByteBuf byteBuf2 = Unpooled.directBuffer();
        compositeByteBuf1.addComponent(byteBuf2);
        byteBuf2.writeBytes("bodybody".getBytes());
        compositeByteBuf1.addComponent(true,byteBuf2);
        System.out.println(compositeByteBuf1.readerIndex()+":"+compositeByteBuf1.writerIndex());
    }
}
