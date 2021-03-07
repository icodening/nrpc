package cn.icodening.rpc.plugin.lifecycle;

import cn.icodening.rpc.core.event.Event;
import cn.icodening.rpc.core.event.EventPublisher;
import cn.icodening.rpc.core.extension.ExtensionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author icodening
 * @date 2021.01.30
 */
public class NrpcShutdownHook extends Thread {

    private static final NrpcShutdownHook NRPC_SHUTDOWN_HOOK = new NrpcShutdownHook();

    private EventPublisher eventPublisher;

    private final List<NrpcShutdownCallable> shutdownCallableList = Collections.synchronizedList(new ArrayList<>());

    public static NrpcShutdownHook getInstance() {
        return NRPC_SHUTDOWN_HOOK;
    }

    private NrpcShutdownHook() {
    }

    public void addShutdownCallable(NrpcShutdownCallable shutdownCallable) {
        this.shutdownCallableList.add(shutdownCallable);
    }

    @Override
    public void run() {
        if (this.eventPublisher == null) {
            this.eventPublisher = ExtensionLoader.getExtensionLoader(EventPublisher.class).getExtension();
        }
        shutdownCallableList.forEach(NrpcShutdownCallable::onShutdown);
        Event shutdownEvent = new ShutdownEvent();
        this.eventPublisher.publish(shutdownEvent);
    }
}
