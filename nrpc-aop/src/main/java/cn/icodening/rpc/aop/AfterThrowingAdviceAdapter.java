package cn.icodening.rpc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * @author icodening
 * @date 2021.01.09
 */
class AfterThrowingAdviceAdapter implements AdvisorAdapter {

    @Override
    public boolean support(Advice advice) {
        return advice instanceof AfterThrowingAdvice;
    }

    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        return new AfterThrowingAdviceAdviceInterceptor((AfterThrowingAdvice) advisor.getAdvice());
    }
}
