package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.Node;
import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.codec.ServerCodec;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * RPC服务端
 *
 * @author icodening
 * @date 2021.01.24
 */
public interface Server extends Boot, Node {

    /**
     * 获得所有管道
     *
     * @return
     */
    Collection<NrpcChannel> getChannels();

    NrpcChannel getChannel(InetSocketAddress remoteAddress);

    /**
     * 获得一个ChannelHandler
     *
     * @return ChannelHandler
     */
    NrpcChannelHandler getNrpcChannelHandler();

    ServerCodec getServerCodec();

}
