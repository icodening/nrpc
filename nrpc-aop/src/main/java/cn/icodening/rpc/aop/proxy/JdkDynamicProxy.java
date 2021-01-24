package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.core.util.ReflectUtil;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.03
 */
public class JdkDynamicProxy extends AbstractProxy implements InvocationHandler {

    private Class<?>[] interfaces;

    private ClassLoader classLoader;

    public JdkDynamicProxy(Object target) {
        this(target, target.getClass().getInterfaces());
    }

    public JdkDynamicProxy(AopConfig config) {
        super(config);
        this.interfaces = ReflectUtil.getAllInterfaces(config.getTarget().getClass());
        this.classLoader = config.getTarget().getClass().getClassLoader();
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public JdkDynamicProxy(Object target, Class<?>[] interfaces) {
        super(target);
        this.interfaces = interfaces;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (EXCLUDE_METHOD_NAMES.contains(method.getName())) {
            return method.invoke(getTarget(), args);
        }
        List<MethodInterceptor> interceptors = getAopConfig().getInterceptors(method, getTarget().getClass());
        DefaultMethodInvocation methodInvocation =
                new DefaultMethodInvocation(getTarget(), getTarget().getClass(), method, args, interceptors);
        return methodInvocation.proceed();
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(classLoader, interfaces, this);
    }
}
