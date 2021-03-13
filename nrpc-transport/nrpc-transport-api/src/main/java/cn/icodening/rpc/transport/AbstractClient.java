package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;

/**
 * @author icodening
 * @date 2021.03.10
 */
public abstract class AbstractClient extends AbstractBootAdapter implements Client {

    private final NrpcChannelHandler nrpcChannelHandler;

    private final URL url;

    protected AbstractClient(URL url, NrpcChannelHandler nrpcChannelHandler) {
        this.url = url;
        this.nrpcChannelHandler = nrpcChannelHandler;
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
