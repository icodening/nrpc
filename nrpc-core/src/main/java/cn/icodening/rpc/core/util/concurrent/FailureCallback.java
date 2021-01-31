package cn.icodening.rpc.core.util.concurrent;

/**
 * @author icodening
 * @date 2021.01.31
 */
@FunctionalInterface
public interface FailureCallback {

    /**
     * 失败时回调
     *
     * @param throwable 抛出的异常
     */
    void onFailure(Throwable throwable);
}
