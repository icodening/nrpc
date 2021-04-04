package cn.icodening.rpc.config.runner;

import cn.icodening.rpc.config.*;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.plugin.lifecycle.NrpcShutdownHook;
import cn.icodening.rpc.registry.Registry;
import cn.icodening.rpc.registry.RegistryFactory;
import cn.icodening.rpc.registry.RegistryKeyConstant;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务注册到注册中心
 *
 * @author icodening
 * @date 2021.03.14
 */
public class ServiceRegistrationRunner extends AbstractBootAdapter implements NrpcRunner {

    private static final Logger LOGGER = Logger.getLogger(ServiceRegistrationRunner.class);

    @Override
    protected void doStart() {
        String localIp = System.getProperty("local.ip");
        List<RegistryConfig> registryConfigs = NrpcBootstrap.getInstance().getRegistryConfigs();
        List<ServiceConfig> serviceConfigs = NrpcBootstrap.getInstance().getServiceConfigs();
        for (RegistryConfig registryConfig : registryConfigs) {
            URL registryConfigUrl = registryConfig.getUrl();
            String protocol = registryConfigUrl.getProtocol();
            RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(protocol);
            Registry registry = registryFactory.getRegistry(registryConfigUrl);
            for (ServiceConfig serviceConfig : serviceConfigs) {
                List<ProtocolConfig> protocolConfigs = serviceConfig.getProtocolConfigs();
                URL serviceUrl = null;
                for (ProtocolConfig protocolConfig : protocolConfigs) {
                    String protocolName = protocolConfig.getName();
                    String host = protocolConfig.getHost();
                    int port = protocolConfig.getPort();
                    serviceUrl = new URL(protocolName, host, port);
                    serviceUrl.addMetaData("protocol", protocolName);
                }
                if (serviceUrl == null) {
                    //LOGGER ERROR
                    //FIXME I18N
                    throw new NrpcException("service registration runner: service url is null");
                }
                serviceUrl.setHost(localIp);
                serviceUrl.addParameter(RegistryKeyConstant.SERVICE, serviceConfig.getName());
                serviceUrl.addParameter(RegistryKeyConstant.GROUP, serviceConfig.getGroup());
                serviceUrl.addParameter(RegistryKeyConstant.VERSION, serviceConfig.getVersion());
                serviceUrl.addMetaData("interface", serviceConfig.getServiceInterface().getName());
                StringBuilder sb = new StringBuilder();
                Method[] declaredMethods = serviceConfig.getServiceInterface().getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    sb.append(declaredMethod.getName()).append(",");
                }
                serviceUrl.addMetaData("methods", sb.substring(0, sb.length() - 1));
                registry.register(serviceUrl);
                final URL unregisterUrl = serviceUrl;
                NrpcShutdownHook.getInstance().addShutdownCallable(() -> {
                    registry.unregister(unregisterUrl);
                    LOGGER.info("down:" + serviceConfig.getName());
                });
                //FIXME I18N
                LOGGER.info(serviceConfig.getName() + " register success");
            }
        }
    }

    @Override
    public int getPriority() {
        return SERVICE_REGISTRATION_PRIORITY;
    }
}
