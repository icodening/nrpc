package cn.icodening.rpc.spring;

import cn.icodening.rpc.config.ApplicationConfig;
import cn.icodening.rpc.config.NrpcBootstrap;
import cn.icodening.rpc.config.RegistryConfig;
import cn.icodening.rpc.config.ServiceConfig;
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
        Map<String, ServiceConfig> serviceBeanMap = applicationContext.getBeansOfType(ServiceConfig.class);
        Map<String, RegistryConfig> registryConfigMap = applicationContext.getBeansOfType(RegistryConfig.class);
        Map<String, ApplicationConfig> applicationConfigMap = applicationContext.getBeansOfType(ApplicationConfig.class);
        List<RegistryConfig> registryConfigs = new ArrayList<>(registryConfigMap.values());
        List<ServiceConfig> serviceConfigs = new ArrayList<>(serviceBeanMap.values());
        List<ApplicationConfig> applicationConfigs = new ArrayList<>(applicationConfigMap.values());
        if (!applicationConfigs.isEmpty()) {
            bootstrap.application(applicationConfigs.get(0));
        }
        bootstrap.services(serviceConfigs)
                .registries(registryConfigs)
                .start();
    }
}
