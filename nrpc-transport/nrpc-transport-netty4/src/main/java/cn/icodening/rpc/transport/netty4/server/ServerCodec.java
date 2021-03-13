package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardRequest;
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
 * @author icodening
 * @date 2021.03.10
 */
public class ServerCodec extends MessageToMessageCodec<ByteBuf, Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, List<Object> out) throws Exception {
        Codec codec = new ExchangeMessageCodec();
        Serialization serialization = new FastJsonSerialization();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        codec.encode(serialization, response, buffer);
        int limit = buffer.flip().limit();
        byte[] bytes = new byte[limit];
        buffer.get(bytes);
        out.add(Unpooled.wrappedBuffer(bytes));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Codec codec = new ExchangeMessageCodec();
        Serialization serialization = new FastJsonSerialization();
        int i = msg.readableBytes();
        byte[] bytes = new byte[i];
        msg.readBytes(bytes);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        StandardRequest standardRequest = new StandardRequest();
        codec.decode(serialization, wrap, standardRequest);
        out.add(standardRequest);
    }
}
