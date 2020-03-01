package com.linxu.netty.samples.endecode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @author linxu
 * @date 2020/2/9
 * <tip>take care of yourself.everything is no in vain.</tip>
 * 利用MESSAGE PACK 实现解码
 *
 * //TODO MessageToMessageDecoder this is  二次解码
 */
public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        System.err.println("now call decoder,buf refc:"+buf.refCnt());
        buf.retain();
        final byte[] array;
        final int length = buf.readableBytes();
        array = new byte[length];
        buf.getBytes(buf.readerIndex(), array, 0, length);
        MessagePack messagePack = new MessagePack();
        list.add(messagePack.read(array));
        buf.release();
    }
}
