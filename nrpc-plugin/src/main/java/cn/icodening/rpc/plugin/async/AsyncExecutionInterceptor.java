package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.aop.EmptyAnnotation;
import cn.icodening.rpc.aop.util.AopUtil;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.ListenableFuture;
import cn.icodening.rpc.core.util.MessageManager;
import cn.icodening.rpc.core.util.ReflectUtil;
import cn.icodening.rpc.core.util.StringUtil;
import cn.icodening.rpc.core.util.concurrent.AsyncTaskExecutor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class AsyncExecutionInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = Logger.getLogger(AsyncExecutionInterceptor.class);

    private final List<AsyncExecutorFactory> asyncExecutorManagerList = ExtensionLoader.getExtensionLoader(AsyncExecutorFactory.class).getAllExtension();

    private final Map<Method, Annotation> annotations;

    private final Map<String, AsyncTaskExecutor> asyncTaskExecutors = new ConcurrentHashMap<>();

    private final AsyncTaskExecutor defaultExecutor = ExtensionLoader.getExtensionLoader(AsyncTaskExecutor.class).getExtension("threadPoolTaskExecutor");

    public AsyncExecutionInterceptor(Map<Method, Annotation> annotations) {
        this.annotations = annotations;
        annotations.forEach((method, annotation) -> {
            if (annotation instanceof EmptyAnnotation) {
                return;
            }
            Async async = (Async) annotation;
            String executorName = async.executorName();
            if (StringUtil.isBlank(executorName)) {
                return;
            }
            for (AsyncExecutorFactory asyncExecutorManager : asyncExecutorManagerList) {
                try {
                    Object target = AopUtil.getProxyTarget(asyncExecutorManager);
                    Method factoryMethod = target.getClass().getDeclaredMethod(executorName);
                    Object ret = factoryMethod.invoke(target);
                    if (ret == null || !Executor.class.isAssignableFrom(ret.getClass())) {
                        String i18nMessage = MessageManager.get("executor.not.found", executorName);
                        throw new NrpcException(i18nMessage);
                    }
                    asyncTaskExecutors.putIfAbsent(executorName, (AsyncTaskExecutor) ret);
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    String i18nMessage = MessageManager.get("executor.not.found", executorName);
                    LOGGER.warn(i18nMessage);
                    asyncTaskExecutors.putIfAbsent(executorName, defaultExecutor);
                } catch (IllegalAccessException e) {
                    LOGGER.error(e);
                }

            }
        });
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        AsyncTaskExecutor executor = getExecutor(invocation.getMethod(), invocation.getThis().getClass());
        Callable<Object> callable = () -> {
            try {
                Object result = invocation.proceed();
                if (result instanceof Future) {
                    return ((Future<?>) result).get();
                }
                return result;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        };
        return doSubmit(callable, executor, invocation.getMethod().getReturnType());
    }

    protected Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                } catch (Throwable ex) {
                    throw new CompletionException(ex);
                }
            }, executor);
        } else if (ListenableFuture.class.isAssignableFrom(returnType)) {
            return executor.submitListenable(task);
        } else if (Future.class.isAssignableFrom(returnType)) {
            return executor.submit(task);
        } else {
            executor.submit(task);
            return null;
        }
    }

    private AsyncTaskExecutor getExecutor(Method method, Class<?> targetClass) {
        Method mostSpecificMethod = ReflectUtil.getMostSpecificMethod(method, targetClass);
        Async async = (Async) annotations.get(mostSpecificMethod);
        if (StringUtil.isBlank(async.executorName())) {
            return defaultExecutor;
        }
        return asyncTaskExecutors.get(async.executorName());
    }
}
