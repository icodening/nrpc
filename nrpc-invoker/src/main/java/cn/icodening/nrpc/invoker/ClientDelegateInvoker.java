package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.Extension;
import cn.icodening.rpc.core.extension.Prototype;
import cn.icodening.rpc.transport.Client;

import java.io.IOException;

/**
 * @author icodening
 * @date 2021.03.21
 */
@Extension(scope = Prototype.PROTOTYPE)
public class ClientDelegateInvoker implements Invoker {

    private Client client;

    private URL remoteUrl;

    @Override
    public void invoke(ExchangeMessage request) {
        if (client == null || !client.isAvailable()) {
            throw new NrpcException("服务不可用");
        }
        client.request((Request) request);

    }

    @Override
    public void setRemoteUrl(URL remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @Override
    public URL getRemoteUrl() {
        return remoteUrl;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.destroy();
        }
    }
}
