package cn.icodening.rpc.plugin.lifecycle;

import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.Lifecycle;
import cn.icodening.rpc.core.util.ReflectUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 初始化/销毁扩展点后置处理器
 *
 * @author icodening
 * @date 2021.03.07
 */
public class InitializerDestroyPostProcessor extends AbstractInitializerDestroyPostProcessor {

    @Override
    protected List<Method> findInitializerMethods(Class<?> clazz) {
        List<Method> initMethods = ReflectUtil.findAnnotationMethods(clazz, PostConstruct.class);
        if (Initializer.class.isAssignableFrom(clazz)) {
            Method initializeMethod = ReflectUtil.findMethod(clazz, "initialize");
            initMethods.add(initializeMethod);
        }
        Collections.reverse(initMethods);
        return initMethods;
    }

    @Override
    protected List<Method> findDestroyMethods(Class<?> clazz) {
        List<Method> destroyMethods = ReflectUtil.findAnnotationMethods(clazz, PreDestroy.class);
        if (Lifecycle.class.isAssignableFrom(clazz)) {
            Method destroyMethod = ReflectUtil.findMethod(clazz, "destroy");
            destroyMethods.add(destroyMethod);
        }
        Collections.reverse(destroyMethods);
        return destroyMethods;
    }

    @Override
    protected Consumer<Object> buildInitConsumer(Method initMethod) {
        return target -> ReflectUtil.invokeMethod(initMethod, target);
    }

    @Override
    protected Consumer<Object> buildDestroyConsumer(Method destroyMethod) {
        return target -> NrpcShutdownHook.getInstance().addShutdownCallable(() -> ReflectUtil.invokeMethod(destroyMethod, target));
    }

}
