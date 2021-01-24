package cn.icodening.rpc.plugin.time;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author icodening
 * @date 2021.01.10
 */
public class PrintTimeInterceptor implements MethodInterceptor {

    private static final String DEFAULT_MSG = "%s 耗时: %s ms";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long begin = System.currentTimeMillis();
        Object proceed = invocation.proceed();
        String prefix = invocation.getThis().getClass().getCanonicalName() + "." + invocation.getMethod().getName();
        String result = String.format(DEFAULT_MSG, prefix, (System.currentTimeMillis() - begin));
        System.out.println(result);
        return proceed;
    }
}
