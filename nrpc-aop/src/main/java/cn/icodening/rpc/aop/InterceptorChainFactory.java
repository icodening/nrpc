package cn.icodening.rpc.aop;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 拦截调用链工厂
 *
 * @author icodening
 * @date 2021.01.09
 */
public interface InterceptorChainFactory {

    /**
     * 根据方法与目标类获取对应的拦截器链
     *
     * @param advisor     通知持有者
     * @param method      被调用的方法
     * @param targetClass 目标类
     * @return 拦截器
     */
    List<MethodInterceptor> getInterceptorChain(Advisor[] advisor, Method method, Class<?> targetClass);
}
