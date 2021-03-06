package cn.icodening.rpc.core.event;

/**
 * 订阅者
 *
 * @author icodening
 * @date 2021.02.06
 */
public interface Subscriber {

    /**
     * 获得订阅的事件类型
     *
     * @return 订阅事件的类型
     */
    Class<? extends Event> getEventType();

    /**
     * 获得该订阅者对应的事件发布者
     *
     * @return 对应的事件发布者
     */
    EventPublisher getEventPublisher();


    /**
     * 订阅者收到事件后的回调
     *
     * @param event 事件
     */
    void onEvent(Event event);
}
