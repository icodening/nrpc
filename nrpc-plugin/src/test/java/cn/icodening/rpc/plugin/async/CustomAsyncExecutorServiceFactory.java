package cn.icodening.rpc.plugin.async;


import cn.icodening.rpc.core.task.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class CustomAsyncExecutorServiceFactory implements AsyncExecutorFactory {

    public Executor myExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadFactory(new ThreadFactory() {
            private final AtomicInteger atomicInteger = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("custom-name-" + atomicInteger.getAndIncrement());
                return thread;
            }
        });

        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
