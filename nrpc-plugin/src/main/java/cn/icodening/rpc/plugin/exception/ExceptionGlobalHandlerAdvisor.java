package cn.icodening.rpc.plugin.exception;

import cn.icodening.rpc.aop.Advisor;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2021.01.25
 */
public class ExceptionGlobalHandlerAdvisor implements Advisor {

    private final Advice nrpcExceptionHandler = new NrpcExceptionGlobalHandler();

    @Override
    public Advice getAdvice() {
        return nrpcExceptionHandler;
    }

    @Override
    public boolean matchMethod(Method method, Class<?> targetClass) {
        return true;
    }
}
