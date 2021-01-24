package cn.icodening.rpc.aop;

import cn.icodening.rpc.core.util.Holder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 方法前置增强拦截器
 *
 * @author icodening
 * @date 2021.01.07
 */
public class AfterReturningAdviceInterceptor implements MethodInterceptor, BeforeAdvice {

    private final AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object returnValue = invocation.proceed();
        Holder<Object> holder = new Holder<>(returnValue);
        advice.afterReturning(holder, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return holder.get();
    }
}
