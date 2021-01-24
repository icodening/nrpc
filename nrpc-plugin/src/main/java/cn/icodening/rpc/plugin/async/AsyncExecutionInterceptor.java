package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.aop.EmptyAnnotation;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.DefaultThreadPoolFactory;
import cn.icodening.rpc.core.util.ReflectUtil;
import cn.icodening.rpc.core.util.StringUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class AsyncExecutionInterceptor implements MethodInterceptor {

    private final List<AsyncExecutorFactory> asyncExecutorManagerList = ExtensionLoader.getExtensionLoader(AsyncExecutorFactory.class).getAllExtension();

    private final ExecutorService defaultExecutor = DefaultThreadPoolFactory.create("nrpc-async", 200);

    private final Map<Method, Annotation> annotations;

    private final Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    public AsyncExecutionInterceptor(Map<Method, Annotation> annotations) {
        this.annotations = annotations;
        annotations.forEach((method, annotation) -> {
            if (annotation instanceof EmptyAnnotation) {
                return;
            }
            Async async = (Async) annotation;
            String executorName = async.executorName();
            for (AsyncExecutorFactory asyncExecutorManager : asyncExecutorManagerList) {
                try {
                    Method factoryMethod = asyncExecutorManager.getClass().getDeclaredMethod(executorName);
                    Object ret = factoryMethod.invoke(asyncExecutorManager);
                    if (ret == null || !ExecutorService.class.isAssignableFrom(ret.getClass())) {
                        throw new RuntimeException("(" + executorName + ") executor not found");
                    }
                    ExecutorService executorService = (ExecutorService) ret;
                    executorServiceMap.putIfAbsent(executorName, executorService);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    executorServiceMap.putIfAbsent(executorName, defaultExecutor);
                } catch (IllegalAccessException e) {
                    //FIXME LOG
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ExecutorService executorService = getExecutorService(invocation.getMethod(), invocation.getThis().getClass());
        return executorService.submit(() -> {
            try {
                return invocation.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        });
    }

    private ExecutorService getExecutorService(Method method, Class<?> targetClass) {
        Method mostSpecificMethod = ReflectUtil.getMostSpecificMethod(method, targetClass);
        Async async = (Async) annotations.get(mostSpecificMethod);
        if (StringUtil.isBlank(async.executorName())) {
            return defaultExecutor;
        }
        return executorServiceMap.get(async.executorName());
    }
}
