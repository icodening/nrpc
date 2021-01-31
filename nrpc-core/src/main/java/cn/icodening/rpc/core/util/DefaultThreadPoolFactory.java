package cn.icodening.rpc.core.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2020.12.30
 */
public class DefaultThreadPoolFactory {

    private static final int MIN_THREAD_POOL_SIZE = 1;

    private DefaultThreadPoolFactory() {
    }

    public static ExecutorService create(final String threadName, int coreSize) {
        return create(threadName, coreSize, coreSize);
    }

    public static ExecutorService create(final String threadName, int coreSize, int maxPoolSize) {
        return create(threadName, coreSize, maxPoolSize, 0);
    }

    public static ExecutorService create(final String threadName, int coreSize, int maxPoolSize, int queueSize) {
        return create(coreSize, maxPoolSize, queueSize, new ThreadFactory() {
            private final AtomicInteger integer = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(threadName + "-" + integer.getAndIncrement());
                return thread;
            }
        });
    }

    public static ExecutorService create(int coreSize, int maxPoolSize, int queueSize, ThreadFactory threadFactory) {
        coreSize = coreSize <= 0 ? MIN_THREAD_POOL_SIZE : coreSize;
        maxPoolSize = Math.max(maxPoolSize, coreSize);
        BlockingQueue<Runnable> blockingQueue =
                queueSize <= 0 ?
                        new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(coreSize, maxPoolSize, 0, TimeUnit.SECONDS, blockingQueue, threadFactory);
    }
}
