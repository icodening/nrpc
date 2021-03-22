package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.transport.AbstractServer;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class Netty4Server extends AbstractServer {
    private static final Logger LOGGER = Logger.getLogger(Netty4Server.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap bootstrap;

    public Netty4Server(URL url, ServerCodec serverCodec, NrpcChannelHandler nrpcChannelHandler) {
        super(url, serverCodec, nrpcChannelHandler);
    }

    @Override
    protected void doInitialize() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
        final Netty4ServerChannelHandler channelHandler = new Netty4ServerChannelHandler(getUrl(), getNrpcChannelHandler());
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new Netty4ServerDecoder(getServerCodec()));
                        pipeline.addLast(new Netty4ServerEncoder(getServerCodec()));
                        pipeline.addLast(channelHandler);
                    }
                });
    }

    @Override
    protected void doStart() {
        try {
            ChannelFuture future = bootstrap.bind(getUrl().getPort()).sync();
            LOGGER.info("启动成功, 端口号: " + getUrl().getPort());
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDestroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
