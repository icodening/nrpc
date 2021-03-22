package cn.icodening.rpc.transport;

import cn.icodening.rpc.common.codec.ClientCodec;
import cn.icodening.rpc.core.Node;
import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.exchange.Request;

/**
 * RPC客户端
 *
 * @author icodening
 * @date 2021.01.24
 */
public interface Client extends Boot, Node {

    /**
     * 获得一个ChannelHandler
     *
     * @return ChannelHandler
     */
    NrpcChannelHandler getNrpcChannelHandler();

    void request(Request request);

    ClientCodec getClientCodec();

}
