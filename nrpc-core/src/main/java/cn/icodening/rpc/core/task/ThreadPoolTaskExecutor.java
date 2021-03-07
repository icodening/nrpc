package cn.icodening.rpc.core.task;

import cn.icodening.rpc.core.Initializer;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO 需要新增线程池销毁功能
 *
 * @author icodening
 * @date 2021.01.31
 */
public class ThreadPoolTaskExecutor implements AsyncTaskExecutor, Initializer {

    private int corePoolSize = 8;

    private int maxPoolSize = corePoolSize;

    private int keepAliveSeconds = 60;

    private int queueCapacity = Integer.MAX_VALUE;

    private TaskDecorator taskDecorator;

    public TaskDecorator getTaskDecorator() {
        return taskDecorator;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    private ThreadFactory threadFactory = new ThreadFactory() {
        private static final String THREAD_NAME = EXECUTOR_THREAD_NAME_PREFIX + "-";

        private final AtomicInteger integer = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(THREAD_NAME + integer.getAndIncrement());
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
        if (taskDecorator != null) {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue, threadFactory) {
                @Override
                public void execute(Runnable command) {
                    Runnable decorated = taskDecorator.decorate(command);
                    super.execute(decorated);
                }
            };
        } else {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue, threadFactory);
        }
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
