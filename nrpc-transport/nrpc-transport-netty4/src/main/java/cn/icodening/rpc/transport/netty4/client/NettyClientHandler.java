package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.NrpcHeaders;
import cn.icodening.rpc.core.exchange.Response;
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
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

    private final NrpcChannelHandler nrpcChannelHandler;

    private final URL url;

    public NettyClientHandler(URL url, NrpcChannelHandler nrpcChannelHandler) {
        this.nrpcChannelHandler = nrpcChannelHandler;
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        NrpcChannel nrpcChannel = NettyChannelRepository.getOrCreate(ctx.channel(), url);
        NrpcHeaders headers = response.getHeaders();
        nrpcChannelHandler.received(nrpcChannel, response);
        System.out.println(headers);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

}
