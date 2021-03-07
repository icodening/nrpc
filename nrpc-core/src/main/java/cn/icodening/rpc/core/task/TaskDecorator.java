package cn.icodening.rpc.core.task;

/**
 * Runnable装饰器
 *
 * @author icodening
 * @date 2021.02.03
 */
@FunctionalInterface
public interface TaskDecorator {

    /**
     * 装饰Runnable
     *
     * @param runnable 任务runnable
     * @return 装饰后的任务runnable
     */
    Runnable decorate(Runnable runnable);
}
