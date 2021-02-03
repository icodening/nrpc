package cn.icodening.rpc.plugin.async;


import cn.icodening.rpc.core.task.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class CustomAsyncExecutorServiceFactory implements AsyncExecutorFactory {

    public Executor myExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.MAX_VALUE);
        threadPoolTaskExecutor.setThreadFactory((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("custom-name");
            return thread;
        });
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
