package cn.icodening.rpc.aop;

import cn.icodening.rpc.core.util.Holder;

import java.lang.reflect.Method;

/**
 * 方法返回后置增强
 *
 * @author icodening
 * @date 2021.01.06
 */
public interface AfterReturningAdvice extends AfterAdvice {


    /**
     * 目标方法被调用后执行
     *
     * @param returnValueHolder 方法被调用后的返回值
     * @param method      被调用的方法
     * @param args        方法参数
     * @param target      目标对象
     * @throws Throwable 异常
     */
    void afterReturning(Holder<Object> returnValueHolder, Method method, Object[] args, Object target) throws Throwable;
}
