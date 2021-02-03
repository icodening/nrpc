package cn.icodening.rpc.core.task;

import java.util.concurrent.Future;

/**
 * @author icodening
 * @date 2021.01.31
 */
public interface ListenableFuture<V> extends Future<V> {

    /**
     * 添加成功回调
     *
     * @param successCallback 成功回调函数
     */
    void addSuccessCallback(SuccessCallback<V> successCallback);

    /**
     * 添加异常时的回调
     *
     * @param failureCallback 失败回调函数
     */
    void addFailureCallback(FailureCallback failureCallback);
}
