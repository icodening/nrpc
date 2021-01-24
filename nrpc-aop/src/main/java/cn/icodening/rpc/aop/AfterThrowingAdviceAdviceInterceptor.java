package cn.icodening.rpc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常拦截器
 * 一个方法调用抛出异常可能有多种类型，所以这里和其他拦截器不一样，并没有限制方法,
 * 但需要按照一定格式编写代码,并使用 {@link AfterThrowingAdvice} 标记接口
 * void afterThrowing([Method, args, target], ThrowableSubclass).
 * <p>
 * public void afterThrowing(Method method, Object[] args, Object target, NullPointerException npe){}
 * public void afterThrowing(NullPointerException npe){}
 * 当使用两种不同的写法但却捕获同一种异常时，只会有一个生效!!!
 *
 * @author icodening
 * @date 2021.01.07
 */
public class AfterThrowingAdviceAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    private final Map<Class<?>, Method> exceptionHandlerMap = new HashMap<>();

    private final AfterThrowingAdvice afterThrowingAdvice;

    public AfterThrowingAdviceAdviceInterceptor(AfterThrowingAdvice afterThrowingAdvice) {
        this.afterThrowingAdvice = afterThrowingAdvice;
        Class<? extends AfterThrowingAdvice> adviceClass = this.afterThrowingAdvice.getClass();
        Method[] methods = adviceClass.getMethods();
        for (Method method : methods) {
            if (!AfterThrowingAdvice.EXCEPTION_HANDLER_METHOD_NAME.equals(method.getName())) {
                continue;
            }
            if (method.getParameterCount() == 1
                    || method.getParameterCount() == 4) {
                Class<?> throwableParamClazz = method.getParameterTypes()[method.getParameterCount() - 1];
                if (Throwable.class.isAssignableFrom(throwableParamClazz)) {
                    this.exceptionHandlerMap.put(throwableParamClazz, method);
                }
            }

        }
    }


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            Method exceptionHandler = getExceptionHandler(throwable);
            if (exceptionHandler != null) {
                invokeHandlerMethod(invocation, throwable, exceptionHandler);
            }
            throw throwable;
        }
    }

    private Method getExceptionHandler(Throwable throwable) {
        Class<?> exceptionClass = throwable.getClass();
        Method handler = this.exceptionHandlerMap.get(exceptionClass);
        //循环获取，防止注册的异常的子类被忽略
        while (handler == null && exceptionClass != Throwable.class) {
            exceptionClass = exceptionClass.getSuperclass();
            handler = this.exceptionHandlerMap.get(exceptionClass);
        }
        return handler;
    }

    private void invokeHandlerMethod(MethodInvocation invocation, Throwable ex, Method exceptionHandlerMethod) throws Throwable {
        Object[] args;
        if (exceptionHandlerMethod.getParameterCount() == 1) {
            args = new Object[]{ex};
        } else {
            args = new Object[]{invocation.getMethod(), invocation.getArguments(), invocation.getThis(), ex};
        }
        try {
            exceptionHandlerMethod.invoke(this.afterThrowingAdvice, args);
        } catch (InvocationTargetException targetEx) {
            throw targetEx.getTargetException();
        }
    }
}
