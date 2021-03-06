package cn.icodening.rpc.core.event;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author icodening
 * @date 2021.02.06
 */
public abstract class AbstractEventPublisher implements EventPublisher {

    private final ConcurrentMap<Class<? extends Event>, Set<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public Set<Subscriber> getSubscribers(Class<? extends Event> eventType) {
        Set<Subscriber> subscribers = this.subscribers.get(eventType);
        if (subscribers == null) {
            return Collections.emptySet();
        }
        return subscribers;
    }

    public void addSubscriber(Class<? extends Event> eventType, Subscriber subscriber) {
        Set<Subscriber> registeredSubscribers = this.subscribers.get(eventType);
        if (registeredSubscribers == null) {
            this.subscribers.putIfAbsent(eventType, new CopyOnWriteArraySet<>());
            registeredSubscribers = this.subscribers.get(eventType);
        }
        registeredSubscribers.add(subscriber);
    }
}
