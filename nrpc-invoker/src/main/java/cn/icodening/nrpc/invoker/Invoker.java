package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.extension.Extensible;

import java.io.Closeable;

/**
 * @author icodening
 * @date 2021.03.21
 */
@Extensible
public interface Invoker extends Closeable {

    void invoke(ExchangeMessage message);

    void setRemoteUrl(URL url);

    URL getRemoteUrl();

}
