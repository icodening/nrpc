package cn.icodening.rpc.plugin;

import cn.icodening.rpc.core.ObjectFactory;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2021.01.19
 */
public abstract class AbstractCreatedExtensionPostProcessor implements CreatedExtensionPostProcessor {

    @Override
    public Advice getAdvice() {
        return this;
    }

    @Override
    public boolean matchMethod(Method method, Class<?> targetClass) {
        return ObjectFactory.class.isAssignableFrom(targetClass);
    }

}
