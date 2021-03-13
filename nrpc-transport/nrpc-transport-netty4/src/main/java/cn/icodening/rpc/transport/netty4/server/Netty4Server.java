package cn.icodening.rpc.transport.netty4.server;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.transport.AbstractServer;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class Netty4Server extends AbstractServer {
    private static final Logger LOGGER = Logger.getLogger(Netty4Server.class);
    //    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture future;


    public Netty4Server(URL url, NrpcChannelHandler nrpcChannelHandler) {
        super(url, nrpcChannelHandler);
    }

    @Override
    protected void doInitialize() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
        final NettyServerChannelHandler channelHandler = new NettyServerChannelHandler(getUrl(), getNrpcChannelHandler());
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                Integer.MAX_VALUE, 0, 4, 0, 4, false));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new ServerCodec());
                        pipeline.addLast(channelHandler);
                    }
                });
    }

    @Override
    protected void doStart() {
        try {
            future = bootstrap.bind(getUrl().getPort()).sync();
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
