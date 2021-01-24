package cn.icodening.rpc.aop.proxy;

/**
 * @author icodening
 * @date 2021.01.03
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public AopProxy createProxy(AopConfig aopConfig) {
        return new JdkDynamicProxy(aopConfig);
    }
}
