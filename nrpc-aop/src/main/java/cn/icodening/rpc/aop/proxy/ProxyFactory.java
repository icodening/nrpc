package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.01.03
 */
@Extensible("jdk")
public interface ProxyFactory {

    AopProxy createProxy(AopConfig aopConfig);

}
