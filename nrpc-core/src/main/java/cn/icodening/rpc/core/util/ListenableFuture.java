package cn.icodening.rpc.core.util;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author icodening
 * @date 2021.01.31
 */
public interface ListenableFuture<V> extends Future<V> {

    void onSuccess(Consumer<V> consumer);

    void onFailure(Consumer<Throwable> consumer);
}
