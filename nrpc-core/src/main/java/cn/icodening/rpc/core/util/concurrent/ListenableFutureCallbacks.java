package cn.icodening.rpc.core.util.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.31
 */
public class ListenableFutureCallbacks<T> {

    private State state = State.NEW;

    private Object result;

    private final List<SuccessCallback<T>> successCallbacks = new ArrayList<>(1);

    private final List<FailureCallback> failureCallbacks = new ArrayList<>(1);

    public synchronized void addSuccessCallback(SuccessCallback<T> successCallback) {
        if (State.FAILURE.equals(state)) {
            return;
        }
        if (State.NEW.equals(state)) {
            successCallbacks.add(successCallback);
        } else if (State.SUCCESS.equals(state)) {
            successCallbacks.add(successCallback);
            successCallbacks.forEach(this::notifySuccess);
        }
    }

    public synchronized void addFailureCallback(FailureCallback failureCallback) {
        if (State.SUCCESS.equals(state)) {
            return;
        }
        if (State.NEW.equals(state)) {
            failureCallbacks.add(failureCallback);
        } else if (State.FAILURE.equals(state)) {
            failureCallbacks.add(failureCallback);
            failureCallbacks.forEach(this::notifyFailure);
        }
    }

    public synchronized void success(Object result) {
        this.result = result;
        this.state = State.SUCCESS;
        successCallbacks.forEach(this::notifySuccess);
    }

    public synchronized void failure(Object ex) {
        this.result = ex;
        this.state = State.FAILURE;
        failureCallbacks.forEach(this::notifyFailure);
    }

    private void notifySuccess(SuccessCallback<T> successCallback) {
        try {
            successCallback.onSuccess((T) result);
        } catch (Throwable ignore) {
        }
    }

    private void notifyFailure(FailureCallback failureCallback) {
        try {
            failureCallback.onFailure((Throwable) result);
        } catch (Throwable ignore) {
        }
    }

    private enum State {NEW, SUCCESS, FAILURE}
}
