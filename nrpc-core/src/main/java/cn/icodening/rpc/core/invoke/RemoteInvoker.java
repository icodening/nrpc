package cn.icodening.rpc.core.invoke;

import cn.icodening.rpc.core.exchange.Request;

/**
 * @author icodening
 * @date 2021.03.17
 */
public interface RemoteInvoker {

    void invoke(Request request);
}
