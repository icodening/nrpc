package cn.icodening.rpc.aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.03
 */
public class CglibDynamicProxy extends AbstractProxy implements net.sf.cglib.proxy.MethodInterceptor {

    public CglibDynamicProxy(AopConfig config) {
        super(config);
    }

    @Override
    public Object getProxy() {
        return Enhancer.create(getTarget().getClass(), this);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        List<MethodInterceptor> interceptors = getAopConfig().getInterceptors(method, getTarget().getClass());
        DefaultMethodInvocation cglibMethodInvocation
                = new DefaultMethodInvocation(getTarget(), getTarget().getClass(), method, args, interceptors);
        return cglibMethodInvocation.proceed();
    }
}
