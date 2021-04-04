package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.core.codec.ServerCodec;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.serialization.FastJsonSerialization;
import cn.icodening.rpc.transport.netty4.Netty4Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author icodening
 * @date 2021.03.20
 */
public class Netty4ServerEncoder extends MessageToByteEncoder<Response> {

    private final ServerCodec serverCodec;

    public Netty4ServerEncoder(ServerCodec serverCodec) {
        this.serverCodec = serverCodec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        FastJsonSerialization fastJsonSerialization = new FastJsonSerialization();
        Netty4Buffer netty4Buffer = new Netty4Buffer(out);
        serverCodec.encode(fastJsonSerialization, response, netty4Buffer);
    }
}
