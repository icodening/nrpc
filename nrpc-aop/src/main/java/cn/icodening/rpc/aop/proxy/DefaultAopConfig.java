package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.aop.DefaultInterceptorChainFactory;
import cn.icodening.rpc.core.util.ReflectUtil;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.01.10
 */
public class DefaultAopConfig implements AopConfig {

    private final Map<Method, List<MethodInterceptor>> interceptorsCache = new ConcurrentHashMap<>();

    private final Object target;

    private final List<Advisor> advisors;


    public DefaultAopConfig(Object target, List<Advisor> advisors) {
        this.target = target;
        this.advisors = advisors;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public List<Advisor> getAdvisors() {
        return advisors;
    }

    @Override
    public List<MethodInterceptor> getInterceptors(Method method, Class<?> targetClass) {
        //FIXME 优化效率
        Method mostSpecificMethod = ReflectUtil.getMostSpecificMethod(method, targetClass);
        List<MethodInterceptor> interceptors = interceptorsCache.get(mostSpecificMethod);
        if (interceptors == null) {
            interceptors = DefaultInterceptorChainFactory.getInterceptors(
                    advisors.toArray(new Advisor[0]),
                    mostSpecificMethod,
                    targetClass);
            interceptorsCache.putIfAbsent(mostSpecificMethod, interceptors);
        }
        return interceptors;
    }

}
