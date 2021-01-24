package cn.icodening.rpc.plugin.boot;

import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.util.Holder;
import cn.icodening.rpc.plugin.AbstractCreatedExtensionPostProcessor;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2021.01.22
 */
public class SmartDestroyBootAdvisor extends AbstractCreatedExtensionPostProcessor {

    @Override
    public boolean supportReturnType(Class<?> clazz) {
        if(clazz == null){
            return false;
        }
        return Boot.class.isAssignableFrom(clazz);
    }

    @Override
    public void afterReturning(Holder<Object> returnValueHolder, Method method, Object[] args, Object target) throws Throwable {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ((Boot) returnValueHolder.get()).destroy()));
    }
}
