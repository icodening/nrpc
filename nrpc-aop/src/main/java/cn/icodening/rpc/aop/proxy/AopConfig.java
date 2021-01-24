package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.aop.Advisor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AOP切面配置
 *
 * @author icodening
 * @date 2021.01.06
 */
public interface AopConfig {

    /**
     * 获取目标对象
     *
     * @return 目标对象
     */
    Object getTarget();

    /**
     * 获取通知链
     *
     * @return 当前目标对象的所有Advisor
     */
    List<Advisor> getAdvisors();

    /**
     * 根据方法、目标类获取对应的拦截链
     *
     * @param method      当前被执行的方法
     * @param targetClass 目标类
     * @return 拦截链
     */
    List<MethodInterceptor> getInterceptors(Method method, Class<?> targetClass);

}
