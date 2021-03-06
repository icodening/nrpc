package cn.icodening.rpc.core.event;

import cn.icodening.rpc.core.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author icodening
 * @date 2021.02.06
 */
public class DefaultSubscriber implements Subscriber {

    /**
     * 回调方法
     */
    private final Method method;

    private final EventPublisher eventPublisher;

    private final Class<? extends Event> eventType;

    private final Object listener;

    public DefaultSubscriber(EventPublisher eventPublisher, Object listener, Method method, Class<? extends Event> eventType) {
        this.method = method;
        this.listener = listener;
        this.eventPublisher = eventPublisher;
        this.eventType = eventType;
    }

    @Override
    public Class<? extends Event> getEventType() {
        return eventType;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void onEvent(Event event) {
        try {
            ReflectUtil.makeAccessible(method);
            method.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultSubscriber that = (DefaultSubscriber) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(eventPublisher, that.eventPublisher) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, eventPublisher, eventType, listener);
    }
}
