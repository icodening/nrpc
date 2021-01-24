package cn.icodening.rpc.aop.proxy;

/**
 * @author icodening
 * @date 2021.01.03
 */
public interface AopProxy {

    Object getTarget();

    Object getProxy();
}
