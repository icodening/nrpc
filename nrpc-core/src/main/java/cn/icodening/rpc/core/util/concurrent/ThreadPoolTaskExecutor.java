package cn.icodening.rpc.core.util.concurrent;

import cn.icodening.rpc.core.Initializer;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2021.01.31
 */
public class ThreadPoolTaskExecutor implements AsyncTaskExecutor, Initializer {

    private int corePoolSize = 8;

    private int maxPoolSize = corePoolSize;

    private int keepAliveSeconds = 60;

    private int queueCapacity = Integer.MAX_VALUE;

    private ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger(0);
        private final String threadName = EXECUTOR_THREAD_NAME_PREFIX + "-";

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(threadName + integer.getAndIncrement());
            return thread;
        }
    };

    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void initialize() {
        BlockingQueue<Runnable> workQueue;
        if (queueCapacity > 0) {
            workQueue = new LinkedBlockingQueue<>(queueCapacity);
        } else {
            workQueue = new SynchronousQueue<>();
        }
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    public synchronized int getCorePoolSize() {
        return corePoolSize;
    }

    public synchronized void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        if (maxPoolSize < corePoolSize) {
            maxPoolSize = corePoolSize;
        }
    }

    public synchronized int getMaxPoolSize() {
        return maxPoolSize;
    }

    public synchronized void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public synchronized int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public synchronized void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public synchronized int getQueueCapacity() {
        return queueCapacity;
    }

    public synchronized void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public synchronized void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public synchronized ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public synchronized void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
        threadPoolExecutor.execute(future);
        return future;
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask<?> future = new ListenableFutureTask<>(task);
        threadPoolExecutor.execute(future);
        return future;
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
        threadPoolExecutor.execute(future);
        return future;
    }

    @Override
    public void execute(Runnable command) {
        ListenableFutureTask<?> future = new ListenableFutureTask<>(command);
        threadPoolExecutor.execute(future);
    }
}
