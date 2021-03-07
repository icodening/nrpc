package cn.icodening.rpc.plugin.lifecycle;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author icodening
 * @date 2021.03.08
 */
class InitializerDestroyCompositeInvoker {

    private final List<Consumer<Object>> consumers;

    InitializerDestroyCompositeInvoker(List<Consumer<Object>> consumers) {
        this.consumers = consumers;
    }

    public void invoke(Object target) {
        for (Consumer<Object> consumer : consumers) {
            consumer.accept(target);
        }
    }
}
