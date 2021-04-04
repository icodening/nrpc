package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.codec.ClientCodec;
import cn.icodening.rpc.core.codec.ServerCodec;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.10
 */
@Extensible("netty4")
public interface TransportFactory {

    Client createClient(URL url, ClientCodec clientCodec, NrpcChannelHandler nrpcChannelHandler);

    Server createServer(URL url, ServerCodec serverCodec, NrpcChannelHandler nrpcChannelHandler);
}
