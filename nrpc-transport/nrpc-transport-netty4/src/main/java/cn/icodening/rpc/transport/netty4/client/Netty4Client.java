package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.transport.AbstractClient;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class Netty4Client extends AbstractClient {
    private static final Logger LOGGER = Logger.getLogger(Netty4Client.class);

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ChannelPool channelPool;

    private ChannelHandler nettyClientHandler;

    public Netty4Client(URL url, ClientCodec clientCodec, NrpcChannelHandler nrpcChannelHandler) {
        super(url, clientCodec, nrpcChannelHandler);
    }

    @Override
    protected void doInitialize() {
        this.group = new NioEventLoopGroup();
        try {
            nettyClientHandler = new Netty4ClientHandler(getUrl(), getNrpcChannelHandler());
            bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .remoteAddress(getUrl().getHost(), getUrl().getPort());
            LOGGER.info("netty client is init");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doStart() {
        try {
            channelPool = initChannelPool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ChannelPool initChannelPool() {
        return initChannelPool(new AbstractChannelPoolHandler() {
            @Override
            public void channelCreated(Channel ch) {
                initChannelPipeline(ch);
            }
        });
    }

    protected void initChannelPipeline(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new Netty4ClientDecoder(getClientCodec()));
        pipeline.addLast(new Netty4ClientEncoder(getClientCodec()));
        pipeline.addLast(nettyClientHandler);
        LOGGER.info("建立连接成功, 目标: " + ch.localAddress());
    }

    protected ChannelPool initChannelPool(ChannelPoolHandler channelPoolHandler) {
        return new FixedChannelPool(bootstrap,
                channelPoolHandler,
                Integer.parseInt(getUrl().getParameter("max_channel", "10")));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void request(Request request) {
        channelPool.acquire()
                .addListeners((FutureListener<Channel>) future -> {
                    Channel ch = future.get();
                    try {
                        ch.writeAndFlush(request).addListener((ChannelFutureListener) sendFuture -> {
                            if (sendFuture.isSuccess()) {
                                LOGGER.info("发送成功");
                                return;
                            }
                            LOGGER.info("发送失败", sendFuture.cause());
                        });
                    } finally {
                        channelPool.release(ch);
                    }
                });
    }

    @Override
    protected void doDestroy() {
        this.group.shutdownGracefully();
        this.channelPool.close();
    }
}
