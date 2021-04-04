package cn.icodening.rpc.config;

import cn.icodening.rpc.core.event.EventPublisher;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.plugin.lifecycle.NrpcShutdownHook;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author icodening
 * @date 2021.03.12
 */
public class NrpcBootstrap {
    private static final NrpcBootstrap INSTANCE = new NrpcBootstrap();

    private ApplicationConfig applicationConfig;

    private List<ServiceConfig> serviceConfigs = new CopyOnWriteArrayList<>();

    private List<ReferenceConfig> referenceConfigs = new CopyOnWriteArrayList<>();

    private List<RegistryConfig> registryConfigs = new CopyOnWriteArrayList<>();

    private AtomicBoolean status = new AtomicBoolean(false);

    public static NrpcBootstrap getInstance() {
        return INSTANCE;
    }

    private NrpcBootstrap() {
    }

    public NrpcBootstrap application(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        return this;
    }

    public NrpcBootstrap registry(RegistryConfig registryConfig) {
        this.registryConfigs.add(registryConfig);
        return this;
    }

    public NrpcBootstrap registries(List<RegistryConfig> registryConfigs) {
        this.registryConfigs.addAll(registryConfigs);
        return this;
    }

    public NrpcBootstrap service(ServiceConfig serviceConfig) {
        this.serviceConfigs.add(serviceConfig);
        return this;
    }

    public NrpcBootstrap services(List<ServiceConfig> serviceConfigs) {
        this.serviceConfigs.addAll(serviceConfigs);
        return this;
    }

    public NrpcBootstrap reference(ReferenceConfig referenceConfig) {
        this.referenceConfigs.add(referenceConfig);
        return this;
    }

    public NrpcBootstrap references(List<ReferenceConfig> referenceConfigs) {
        this.referenceConfigs.addAll(referenceConfigs);
        return this;
    }

    public NrpcBootstrap start() {
        if (status.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(NrpcShutdownHook.getInstance());
            List<NrpcRunner> runners = ExtensionLoader.getExtensionLoader(NrpcRunner.class).getAllExtension();
            Collections.sort(runners);
            for (NrpcRunner runner : runners) {
                runner.start();
            }
            String publisherName = System.getProperty("nrpc.event.publisher", "syncEventPublisher");
            EventPublisher publisher = ExtensionLoader.getExtensionLoader(EventPublisher.class).getExtension(publisherName);
            publisher.publish(new NrpcStartedEvent("NRPC STATED"));
        }
        return this;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public List<ServiceConfig> getServiceConfigs() {
        return serviceConfigs;
    }

    public List<ReferenceConfig> getReferenceConfigs() {
        return referenceConfigs;
    }

    public List<RegistryConfig> getRegistryConfigs() {
        return registryConfigs;
    }
}
