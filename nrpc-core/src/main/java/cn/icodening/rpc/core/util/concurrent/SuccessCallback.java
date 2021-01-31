package cn.icodening.rpc.core.util.concurrent;

/**
 * @author icodening
 * @date 2021.01.31
 */
@FunctionalInterface
public interface SuccessCallback<T> {

    /**
     * 成功时回调
     *
     * @param value 值
     */
    void onSuccess(T value);
}
