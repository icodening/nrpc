package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.core.util.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * TODO 优化onSuccess、onFailure回调
 *
 * @author icodening
 * @date 2021.01.31
 */
public class AsyncResult<V> implements ListenableFuture<V> {

    private final V value;

    public AsyncResult(V value) {
        super();
        this.value = value;
    }

    @Override
    public void onSuccess(Consumer<V> consumer) {

    }

    @Override
    public void onFailure(Consumer<Throwable> consumer) {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.value;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
