package cn.icodening.rpc.core.event;

/**
 * @author icodening
 * @date 2021.02.06
 */
public interface EventDispatcher {

    /**
     * 分发事件
     *
     * @param event       事件
     * @param subscribers 事件对应的订阅者
     */
    void dispatch(Event event, Iterable<Subscriber> subscribers);
}
