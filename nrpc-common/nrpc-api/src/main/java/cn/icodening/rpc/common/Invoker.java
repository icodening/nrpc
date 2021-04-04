package cn.icodening.rpc.common;

/**
 * @author icodening
 * @date 2021.03.28
 */
public interface Invoker {

    void invoke(Invocation invocation);
}
