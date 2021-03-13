package cn.icodening.rpc.transport.netty4.client;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.transport.AbstractClient;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class Netty4Client extends AbstractClient {
    //        private static final Logger LOGGER = LoggerFactory.getLogger(Netty4Client.class);
    private static final Logger LOGGER = Logger.getLogger(Netty4Client.class);

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private Channel channel;

    public Netty4Client(URL url, NrpcChannelHandler nrpcChannelHandler) {
        super(url, nrpcChannelHandler);
    }

    @Override
    protected void doInitialize() {
        this.group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            ChannelHandler nettyClientHandler = new NettyClientHandler(getUrl(), getNrpcChannelHandler());
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    Integer.MAX_VALUE, 0, 4, 0, 4, false));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new ClientCodec());
                            pipeline.addLast(nettyClientHandler);
                        }
                    });
            LOGGER.info("netty client is init");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doStart() {
        try {
            ChannelFuture connect = bootstrap.connect(getUrl().getHost(), getUrl().getPort()).sync();
            channel = connect.channel();
            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info("连接成功, 目标: " + getUrl().getHost() + ":" + getUrl().getPort());
                    }
                }
            });
            connect.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void request(Request request) {
        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOGGER.info("发送成功");
                    return;
                }
                LOGGER.info("发送失败");
            }
        });
    }

    @Override
    protected void doDestroy() {
        this.group.shutdownGracefully();
    }
}
