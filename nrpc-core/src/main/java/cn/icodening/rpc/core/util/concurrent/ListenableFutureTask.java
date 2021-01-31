package cn.icodening.rpc.core.util.concurrent;

import cn.icodening.rpc.core.util.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

/**
 * @author icodening
 * @date 2021.01.31
 */
public class ListenableFutureTask<V> extends FutureTask<V> implements ListenableFuture<V> {

    public ListenableFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    public ListenableFutureTask(Runnable runnable) {
        super(runnable, null);
    }

    public ListenableFutureTask(Callable<V> callable) {
        super(callable);
    }

    private Consumer<V> successConsumer;

    private Consumer<Throwable> throwableConsumer;

    @Override
    protected void done() {
        try {
            V v = get();
            if (successConsumer != null) {
                successConsumer.accept(v);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            if (throwableConsumer != null) {
                throwableConsumer.accept(e);
            }
        }
    }

    @Override
    public void onSuccess(Consumer<V> successConsumer) {
        this.successConsumer = successConsumer;
    }

    @Override
    public void onFailure(Consumer<Throwable> throwableConsumer) {
        this.throwableConsumer = throwableConsumer;
    }
}
