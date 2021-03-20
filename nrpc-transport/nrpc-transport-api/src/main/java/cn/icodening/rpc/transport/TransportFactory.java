package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.10
 */
@Extensible("netty4")
public interface TransportFactory {

    Client createClient(URL url, NrpcChannelHandler nrpcChannelHandler);

    Server createServer(URL url, NrpcChannelHandler nrpcChannelHandler);
}
