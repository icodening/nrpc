package cn.icodening.rpc.plugin.time;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * @author icodening
 * @date 2021.01.10
 */
public class PrintTimeInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = Logger.getLogger(PrintTimeInterceptor.class);
    private static final String DEFAULT_MSG = "%s cost time : %s ms";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long begin = System.currentTimeMillis();
        Object proceed = invocation.proceed();
        String prefix = invocation.getThis().getClass().getCanonicalName() + "." + invocation.getMethod().getName();
        String result = String.format(DEFAULT_MSG, prefix, (System.currentTimeMillis() - begin));
        LOGGER.debug(result);
        return proceed;
    }
}
