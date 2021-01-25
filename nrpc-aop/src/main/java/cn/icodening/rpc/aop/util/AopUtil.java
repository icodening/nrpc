package cn.icodening.rpc.aop.util;

import cn.icodening.rpc.aop.proxy.JdkDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * AOP工具类
 *
 * @author icodening
 * @date 2021.01.26
 */
public class AopUtil {

    private AopUtil() {
    }

    public static Object getProxyTarget(Object object) {
        if (!isProxy(object)) {
            return object;
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
        if (invocationHandler instanceof JdkDynamicProxy) {
            JdkDynamicProxy proxy = (JdkDynamicProxy) invocationHandler;
            return proxy.getTarget();
        }
        return object;
    }

    public static boolean isProxy(Object object) {
        return Proxy.isProxyClass(object.getClass());
    }
}
