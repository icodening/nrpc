package cn.icodening.rpc.core.event;

import cn.icodening.rpc.core.extension.Extensible;

import java.util.concurrent.Executor;

/**
 * 事件发布器
 *
 * @author icodening
 * @date 2021.02.06
 */
@Extensible(value = "syncEventPublisher")
public interface EventPublisher {

    /**
     * 发布一个事件
     *
     * @param event 事件对象
     */
    void publish(Event event);


    /**
     * 注册一个事件监听器
     *
     * @param listener 监听器
     */
    void register(Object listener);

    /**
     * 获取事件发布者的调度器
     *
     * @return 调度器
     */
    Executor getExecutor();
}
