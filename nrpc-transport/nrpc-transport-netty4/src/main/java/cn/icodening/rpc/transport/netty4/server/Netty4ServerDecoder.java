package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.common.serialization.FastJsonSerialization;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.transport.netty4.Netty4Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class Netty4ServerDecoder extends ByteToMessageDecoder {

    private final ServerCodec serverCodec;

    public Netty4ServerDecoder(ServerCodec serverCodec) {
        this.serverCodec = serverCodec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Netty4Buffer netty4Buffer = new Netty4Buffer(msg);
        FastJsonSerialization fastJsonSerialization = new FastJsonSerialization();
        Request request = serverCodec.decode(fastJsonSerialization, netty4Buffer);
        if (request == null) {
            return;
        }
        out.add(request);
    }
}
