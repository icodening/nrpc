package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.core.util.DefaultThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class CustomAsyncExecutorServiceFactory implements AsyncExecutorFactory {

    public ExecutorService myExecutor() {
        return DefaultThreadPoolFactory.create("myThread", 100);
    }
}
