package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.StandardResponse;
import cn.icodening.rpc.transport.codec.Codec;
import cn.icodening.rpc.transport.codec.ExchangeMessageCodec;
import cn.icodening.rpc.transport.serialization.FastJsonSerialization;
import cn.icodening.rpc.transport.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * FIXME 1.buffer缓冲池复用，减少内存消耗  2.serialization应从自身配置中获取
 *
 * @author icodening
 * @date 2021.03.10
 */
public class ClientCodec extends MessageToMessageCodec<ByteBuf, Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
        Codec codec = new ExchangeMessageCodec();
        Serialization serialization = new FastJsonSerialization();
        ByteBuffer allocate = ByteBuffer.allocate(512);
        codec.encode(serialization, msg, allocate);
        int limit = allocate.flip().limit();
        byte[] bytes = new byte[limit];
        allocate.get(bytes);
        out.add(Unpooled.wrappedBuffer(bytes));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Codec codec = new ExchangeMessageCodec();
        Serialization serialization = new FastJsonSerialization();
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        StandardResponse standardResponse = new StandardResponse();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        codec.decode(serialization, buffer, standardResponse);
        out.add(standardResponse);
    }
}
