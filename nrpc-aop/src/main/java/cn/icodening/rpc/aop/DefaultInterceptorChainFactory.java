package cn.icodening.rpc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.09
 */
public class DefaultInterceptorChainFactory implements InterceptorChainFactory {

    private final List<AdvisorAdapter> adapters = new ArrayList<>(3);

    private static final DefaultInterceptorChainFactory CHAIN_FACTORY = new DefaultInterceptorChainFactory();

    public DefaultInterceptorChainFactory() {
        adapters.add(new MethodBeforeAdviceAdapter());
        adapters.add(new AfterReturningAdviceAdapter());
        adapters.add(new AfterThrowingAdviceAdapter());
    }

    public static List<MethodInterceptor> getInterceptors(Advisor[] advisors, Method method, Class<?> targetClass) {
        return CHAIN_FACTORY.getInterceptorChain(advisors, method, targetClass);
    }

    @Override
    public List<MethodInterceptor> getInterceptorChain(Advisor[] advisors, Method method, Class<?> targetClass) {
        List<MethodInterceptor> interceptors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            if (advisor.matchMethod(method, targetClass)) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof MethodInterceptor) {
                    interceptors.add((MethodInterceptor) advice);
                }
                for (AdvisorAdapter adapter : this.adapters) {
                    if (adapter.support(advice)) {
                        interceptors.add(adapter.getInterceptor(advisor));
                    }
                }
            }
        }
        return interceptors;
    }
}
