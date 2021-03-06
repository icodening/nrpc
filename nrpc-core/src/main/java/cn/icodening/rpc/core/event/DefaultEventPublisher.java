package cn.icodening.rpc.core.event;

import cn.icodening.rpc.core.util.MessageManager;
import cn.icodening.rpc.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * FIXME 暂不支持注册函数式监听器接口
 *
 * @author icodening
 * @date 2021.02.06
 */
public class DefaultEventPublisher extends AbstractEventPublisher {

    private final EventDispatcher eventDispatcher;

    private final Executor executor;

    public DefaultEventPublisher() {
        this(Runnable::run);
    }

    public DefaultEventPublisher(Executor executor) {
        this(executor, (event, subscribers) -> {
            for (Subscriber subscriber : subscribers) {
                subscriber.onEvent(event);
            }
        });
    }

    public DefaultEventPublisher(Executor executor, EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.executor = executor;
    }

    @Override
    public void publish(Event event) {
        Objects.requireNonNull(event, MessageManager.get("event.required.not.null"));
        Class<? extends Event> eventType = event.getClass();
        Set<Subscriber> subscribers = getSubscribers(eventType);
        getExecutor().execute(() -> eventDispatcher.dispatch(event, subscribers));
    }

    @Override
    public void register(Object listener) {
        Objects.requireNonNull(listener, MessageManager.get("listener.required.not.null"));
        Class<?> listenerClass = listener.getClass();
        List<Method> annotationMethods = ReflectUtil.findAnnotationMethods(listenerClass, Subscribe.class);
        if (annotationMethods.isEmpty()) {
            return;
        }
        for (Method annotationMethod : annotationMethods) {
            Class<?>[] parameterTypes = annotationMethod.getParameterTypes();
            if (parameterTypes.length == 0) {
                continue;
            }
            for (Class<?> parameterType : parameterTypes) {
                if (!Event.class.isAssignableFrom(parameterType)) {
                    continue;
                }
                Subscriber subscriber = createSubscriber(listener, annotationMethod, (Class<? extends Event>) parameterType, this);
                addSubscriber((Class<? extends Event>) parameterType, subscriber);
            }
        }
    }

    private Subscriber createSubscriber(Object listener, Method annotationMethod, Class<? extends Event> eventType, EventPublisher eventPublisher) {
        return new DefaultSubscriber(eventPublisher, listener, annotationMethod, eventType);
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }
}
