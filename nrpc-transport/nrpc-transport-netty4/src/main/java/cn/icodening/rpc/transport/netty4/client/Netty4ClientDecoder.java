package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.common.serialization.FastJsonSerialization;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.transport.netty4.Netty4Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class Netty4ClientDecoder extends ByteToMessageDecoder {

    private final ClientCodec clientCodec;

    public Netty4ClientDecoder(ClientCodec clientCodec) {
        this.clientCodec = clientCodec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Netty4Buffer netty4Buffer = new Netty4Buffer(in);
        FastJsonSerialization fastJsonSerialization = new FastJsonSerialization();
        Response response = clientCodec.decode(fastJsonSerialization, netty4Buffer);
        if (response == null) {
            return;
        }
        out.add(response);
    }
}
