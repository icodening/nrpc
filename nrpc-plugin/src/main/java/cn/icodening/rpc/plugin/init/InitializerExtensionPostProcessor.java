package cn.icodening.rpc.plugin.init;

import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.util.Holder;
import cn.icodening.rpc.plugin.AbstractCreatedExtensionPostProcessor;

import java.lang.reflect.Method;

/**
 * 将可初始化的扩展点进行初始化动作 {@link Initializer#initialize()}
 *
 * @author icodening
 * @date 2021.01.15
 */
public class InitializerExtensionPostProcessor extends AbstractCreatedExtensionPostProcessor {

    @Override
    public boolean supportReturnType(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return Initializer.class.isAssignableFrom(clazz);
    }

    @Override
    public void afterReturning(Holder<Object> returnValueHolder, Method method, Object[] args, Object target) throws Throwable {
        ((Initializer) returnValueHolder.get()).initialize();
    }
}
