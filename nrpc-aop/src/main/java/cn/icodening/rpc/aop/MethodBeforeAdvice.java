package cn.icodening.rpc.aop;

import java.lang.reflect.Method;

/**
 * 方法前置增强
 *
 * @author icodening
 * @date 2021.01.07
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

    /**
     * 方法前置增强
     *
     * @param method 被调用的方法
     * @param args   方法参数
     * @param target 目标对象
     * @throws Throwable 执行异常
     */
    void before(Method method, Object[] args, Object target) throws Throwable;

}
