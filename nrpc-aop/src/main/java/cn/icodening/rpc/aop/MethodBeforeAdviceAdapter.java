package cn.icodening.rpc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * @author icodening
 * @date 2021.01.09
 */
class MethodBeforeAdviceAdapter implements AdvisorAdapter {

    @Override
    public boolean support(Advice advice) {
        return advice instanceof MethodBeforeAdvice;
    }

    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advisor.getAdvice());
    }
}
