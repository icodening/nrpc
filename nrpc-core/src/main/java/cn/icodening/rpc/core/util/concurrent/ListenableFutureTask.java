package cn.icodening.rpc.core.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author icodening
 * @date 2021.01.31
 */
public class ListenableFutureTask<V> extends FutureTask<V> implements ListenableFuture<V> {

    private final ListenableFutureCallbacks<V> listenableFutureCallbacks = new ListenableFutureCallbacks<>();

    public ListenableFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    public ListenableFutureTask(Runnable runnable) {
        super(runnable, null);
    }

    public ListenableFutureTask(Callable<V> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        try {
            V v = get();
            listenableFutureCallbacks.success(v);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            listenableFutureCallbacks.failure(e);
        }
    }

    @Override
    public void addSuccessCallback(SuccessCallback<V> successConsumer) {
        listenableFutureCallbacks.addSuccessCallback(successConsumer);
    }

    @Override
    public void addFailureCallback(FailureCallback throwableConsumer) {
        listenableFutureCallbacks.addFailureCallback(throwableConsumer);
    }
}
