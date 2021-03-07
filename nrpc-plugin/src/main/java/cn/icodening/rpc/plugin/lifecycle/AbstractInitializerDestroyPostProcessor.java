package cn.icodening.rpc.plugin.lifecycle;

import cn.icodening.rpc.aop.util.AopUtil;
import cn.icodening.rpc.core.util.Holder;
import cn.icodening.rpc.plugin.AbstractCreatedExtensionPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 初始化/销毁扩展点后置处理器
 *
 * @author icodening
 * @date 2021.03.07
 */
public abstract class AbstractInitializerDestroyPostProcessor extends AbstractCreatedExtensionPostProcessor {

    private final Map<Class<?>, List<Consumer<Object>>> consumerMap = new ConcurrentHashMap<>();

    @Override
    public boolean supportReturnType(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        List<Method> initMethods = findInitializerMethods(clazz);
        List<Method> destroyMethods = findDestroyMethods(clazz);
        List<Consumer<Object>> consumers = consumerMap.get(clazz);
        if (consumers == null) {
            consumerMap.putIfAbsent(clazz, new ArrayList<>(2));
            consumers = consumerMap.get(clazz);
        }
        for (Method initMethod : initMethods) {
            Consumer<Object> initConsumer = buildInitConsumer(initMethod);
            consumers.add(initConsumer);
        }
        for (Method destroyMethod : destroyMethods) {
            Consumer<Object> destroyConsumer = buildDestroyConsumer(destroyMethod);
            consumers.add(destroyConsumer);
        }
        return (!initMethods.isEmpty() || !destroyMethods.isEmpty());
    }

    @Override
    public void afterReturning(Holder<Object> returnValueHolder, Method method, Object[] args, Object target) throws Throwable {
        Object returnValue = returnValueHolder.get();
        Object invokeTarget = AopUtil.getProxyTarget(returnValue);
        List<Consumer<Object>> consumers = consumerMap.get(invokeTarget.getClass());
        InitializerDestroyCompositeInvoker initializerDestroyCompositeInvoker = new InitializerDestroyCompositeInvoker(consumers);
        initializerDestroyCompositeInvoker.invoke(invokeTarget);
    }

    protected abstract List<Method> findInitializerMethods(Class<?> clazz);

    protected abstract List<Method> findDestroyMethods(Class<?> clazz);

    protected abstract Consumer<Object> buildInitConsumer(Method initMethod);

    protected abstract Consumer<Object> buildDestroyConsumer(Method destroyMethod);
}
