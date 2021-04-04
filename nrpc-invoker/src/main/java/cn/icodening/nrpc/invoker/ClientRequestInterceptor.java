package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * 客户端请求拦截器
 *
 * @author icodening
 * @date 2021.03.21
 */
@Extensible
public interface ClientRequestInterceptor {

    /**
     * 请求拦截
     *
     * @param request 请求
     */
    void intercept(Request request);
}
