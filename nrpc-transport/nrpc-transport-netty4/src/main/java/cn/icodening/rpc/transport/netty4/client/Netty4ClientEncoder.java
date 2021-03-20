package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.serialization.FastJsonSerialization;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.transport.netty4.Netty4Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class Netty4ClientEncoder extends MessageToByteEncoder<Request> {

    private final ClientCodec clientCodec;

    public Netty4ClientEncoder(ClientCodec clientCodec) {
        this.clientCodec = clientCodec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, ByteBuf out) throws Exception {
        FastJsonSerialization fastJsonSerialization = new FastJsonSerialization();
        Netty4Buffer netty4Buffer = new Netty4Buffer(out);
        clientCodec.encode(fastJsonSerialization, request, netty4Buffer);
    }
}
