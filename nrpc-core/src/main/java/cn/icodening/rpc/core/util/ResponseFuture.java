package cn.icodening.rpc.core.util;

import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.task.FailureCallback;
import cn.icodening.rpc.core.task.ListenableFuture;
import cn.icodening.rpc.core.task.ListenableFutureCallbacks;
import cn.icodening.rpc.core.task.SuccessCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author icodening
 * @date 2021.04.03
 */
public class ResponseFuture implements ListenableFuture<Response> {

    private final ListenableFutureCallbacks<Response> listenableFutureCallbacks = new ListenableFutureCallbacks<>();

    private Response response;

    private boolean done = false;

    private final CountDownLatch cdl = new CountDownLatch(1);

    @Override
    public void addSuccessCallback(SuccessCallback<Response> successCallback) {
        listenableFutureCallbacks.addSuccessCallback(successCallback);
    }

    @Override
    public void addFailureCallback(FailureCallback failureCallback) {
        listenableFutureCallbacks.addFailureCallback(failureCallback);
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
        return done;
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        if (isDone()) {
            return response;
        }
        cdl.await();
        return response;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (isDone()) {
            return response;
        }
        cdl.await(timeout, unit);
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
        this.done = true;
        this.cdl.countDown();
        listenableFutureCallbacks.success(response);
    }
}
