package cn.icodening.nrpc.invoker;

import java.lang.reflect.Proxy;

/**
 * @author icodening
 * @date 2021.04.03
 */
public class InvokerProxyFactory {

    public static <T> T getProxyInvoker(Cluster cluster, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new NrpcInvoker(cluster));
    }
}
