package cn.icodening.rpc.plugin;

import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.aop.proxy.AopProxy;
import cn.icodening.rpc.aop.proxy.DefaultAopConfig;
import cn.icodening.rpc.aop.proxy.ProxyFactory;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.Holder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展点代理构造器
 *
 * @author icodening
 * @date 2021.01.19
 */
public class ExtensionProxyCreator extends AbstractCreatedExtensionPostProcessor {

    /**
     * Key：扩展点class
     * Value：扩展点通知链
     */
    private final Map<Class<?>, List<Advisor>> classAdvisorsMap = new ConcurrentHashMap<>();

    /**
     * 加载所有通知链
     */
    private final List<Advisor> advisors = ExtensionLoader.getExtensionLoader(Advisor.class).getAllExtension();

    private final ProxyFactory defaultProxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension();

    @Override
    public boolean supportReturnType(Class<?> clazz) {
        Class<?> temp = clazz;
        Set<Advisor> ads = new HashSet<>(4);
        boolean found = false;
        while (!Object.class.equals(temp)) {
            Method[] declaredMethods = clazz.getMethods();
            for (Advisor advisor : advisors) {
                for (Method method : declaredMethods) {
                    if (advisor.matchMethod(method, clazz)) {
                        ads.add(advisor);
                    }
                }
            }
            temp = temp.getSuperclass();
        }
        if (ads.size() > 0) {
            found = true;
            classAdvisorsMap.putIfAbsent(clazz, new ArrayList<>(ads));
        }
        return found;
    }

    @Override
    public void afterReturning(Holder<Object> returnValueHolder, Method method, Object[] args, Object target) throws Throwable {
        List<Advisor> advisors = classAdvisorsMap.get(returnValueHolder.get().getClass());
        DefaultAopConfig config = new DefaultAopConfig(returnValueHolder.get(), advisors);
        //FIXME 暂无配置代理方式的入口，只能默认使用JDK动态代理
        AopProxy proxy = defaultProxyFactory.createProxy(config);
        returnValueHolder.set(proxy.getProxy());
    }

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }
}
