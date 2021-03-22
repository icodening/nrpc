package cn.icodening.rpc.transport;

import cn.icodening.rpc.common.codec.ServerCodec;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author icodening
 * @date 2021.03.10
 */
public abstract class AbstractServer extends AbstractBootAdapter implements Server {
    private final NrpcChannelHandler nrpcChannelHandler;

    private final URL url;

    private final ServerCodec serverCodec;

    protected AbstractServer(URL url, ServerCodec serverCodec, NrpcChannelHandler nrpcChannelHandler) {
        this.nrpcChannelHandler = nrpcChannelHandler;
        this.url = url;
        this.serverCodec = serverCodec;
    }


    @Override
    public Collection<NrpcChannel> getChannels() {
        return null;
    }

    @Override
    public NrpcChannel getChannel(InetSocketAddress remoteAddress) {
        return null;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public NrpcChannelHandler getNrpcChannelHandler() {
        return this.nrpcChannelHandler;
    }

    @Override
    public ServerCodec getServerCodec() {
        return this.serverCodec;
    }
}
