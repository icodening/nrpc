package cn.icodening.nrpc.invoker.interceptor;

import cn.icodening.nrpc.invoker.ClientRequestInterceptor;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.ResponseFuture;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.04.03
 */
public class RequestResponseFutureInterceptor implements ClientRequestInterceptor {

    private final LocalCache<Long, ResponseFuture> futureCache;

    private static final Logger LOGGER = Logger.getLogger(RequestResponseFutureInterceptor.class);

    @SuppressWarnings("unchecked")
    public RequestResponseFutureInterceptor() {
        futureCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("responseFutureCache");
    }

    @Override
    public void intercept(Request request) {
        long id = request.getId();
        ResponseFuture responseFuture = new ResponseFuture();
        responseFuture.addSuccessCallback(response ->
                LOGGER.debug("receive response success, request id:" + request.getId()));
        futureCache.set(id, responseFuture);
    }
}
