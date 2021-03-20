package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.transport.NrpcChannel;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import cn.icodening.rpc.transport.netty4.NettyChannelRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author icodening
 * @date 2021.03.10
 */
@ChannelHandler.Sharable
public class Netty4ServerChannelHandler extends SimpleChannelInboundHandler<Request> {

    private final URL url;

    private final NrpcChannelHandler nrpcChannelHandler;

    public Netty4ServerChannelHandler(URL url, NrpcChannelHandler nrpcChannelHandler) {
        this.url = url;
        this.nrpcChannelHandler = nrpcChannelHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        NrpcChannel nrpcChannel = NettyChannelRepository.getOrCreate(ctx.channel(), url);
        nrpcChannelHandler.received(nrpcChannel, request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
