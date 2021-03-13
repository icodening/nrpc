package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.exchange.Response;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author icodening
 * @date 2021.03.10
 */
public abstract class AbstractServer extends AbstractBootAdapter implements Server {
    private final NrpcChannelHandler nrpcChannelHandler;

    private final URL url;

    protected AbstractServer(URL url, NrpcChannelHandler nrpcChannelHandler) {
        this.nrpcChannelHandler = nrpcChannelHandler;
        this.url = url;
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
    public void response(Response response) {
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public NrpcChannelHandler getNrpcChannelHandler() {
        return this.nrpcChannelHandler;
    }
}
