package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.core.util.ReflectUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.10
 */
public class DefaultMethodInvocation implements MethodInvocation {

    private Object target;
    private Class<?> targetClass;
    private Method method;
    private Object[] args;
    private List<MethodInterceptor> chain;

    private int chainIndex = -1;

    public DefaultMethodInvocation(Object target, Class<?> targetClass, Method method, Object[] args, List<MethodInterceptor> chain) {
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.args = args;
        this.chain = chain;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public Object proceed() throws Throwable {
        if (++chainIndex >= chain.size()) {
            Object invoke = null;
            try {
                ReflectUtil.makeAccessible(method);
                invoke = method.invoke(target, args);
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            return invoke;
        }
        MethodInterceptor methodInterceptor = chain.get(chainIndex);
        return methodInterceptor.invoke(this);
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return null;
    }
}
