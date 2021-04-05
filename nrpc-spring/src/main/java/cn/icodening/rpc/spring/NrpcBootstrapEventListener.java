package cn.icodening.rpc.spring;

import cn.icodening.rpc.config.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NrpcBoot Spring事件监听器，监听阶段{@link ContextRefreshedEvent}
 * 当触发事件时将会启动 Nrpc, 参见{@link NrpcBootstrap}
 *
 * @author icodening
 * @date 2021.04.04
 */
public class NrpcBootstrapEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        NrpcBootstrap bootstrap = NrpcBootstrap.getInstance();
        Map<String, ApplicationConfig> applicationConfigMap = applicationContext.getBeansOfType(ApplicationConfig.class);
        Map<String, RegistryConfig> registryConfigMap = applicationContext.getBeansOfType(RegistryConfig.class);
        Map<String, ReferenceConfig> referenceConfigMap = applicationContext.getBeansOfType(ReferenceConfig.class);
        Map<String, ServiceConfig> serviceBeanMap = applicationContext.getBeansOfType(ServiceConfig.class);

        List<ApplicationConfig> applicationConfigs = new ArrayList<>(applicationConfigMap.values());
        List<RegistryConfig> registryConfigs = new ArrayList<>(registryConfigMap.values());
        List<ReferenceConfig> referenceConfigs = new ArrayList<>(referenceConfigMap.values());
        List<ServiceConfig> serviceConfigs = new ArrayList<>(serviceBeanMap.values());

        if (!applicationConfigs.isEmpty()) {
            bootstrap.application(applicationConfigs.get(0));
        }
        for (ReferenceConfig referenceConfig : referenceConfigs) {
            referenceConfig.setRegistryConfigList(registryConfigs);
        }
        bootstrap.references(referenceConfigs)
                .services(serviceConfigs)
                .registries(registryConfigs)
                .start();
    }
}
