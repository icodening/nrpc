package cn.icodening.rpc.core.util.concurrent;

import cn.icodening.rpc.core.extension.Extensible;
import cn.icodening.rpc.core.util.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * 异步线程池
 *
 * @author icodening
 * @date 2021.01.31
 */
@Extensible("threadPoolTaskExecutor")
public interface AsyncTaskExecutor extends Executor {

    String EXECUTOR_THREAD_NAME_PREFIX = "nrpc-async-executor";

    /**
     * 提交任务
     *
     * @param task 异步任务
     * @return Future
     */
    Future<?> submit(Runnable task);

    /**
     * 带返回值的异步任务
     *
     * @param task 异步任务
     * @return Future
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * 提交可监听的异步任务
     *
     * @param task 异步任务
     * @return 可监听的Future
     */
    ListenableFuture<?> submitListenable(Runnable task);

    /**
     * 提交可监听的异步任务
     *
     * @param task 异步任务
     * @return 可监听的Future
     */
    <T> ListenableFuture<T> submitListenable(Callable<T> task);
}
