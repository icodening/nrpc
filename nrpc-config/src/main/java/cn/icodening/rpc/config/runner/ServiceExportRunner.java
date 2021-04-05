package cn.icodening.rpc.config.runner;

import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.common.model.NrpcService;
import cn.icodening.rpc.config.*;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.MessageManager;
import cn.icodening.rpc.transport.Server;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * 服务暴露
 *
 * @author icodening
 * @date 2021.03.14
 */
public class ServiceExportRunner extends AbstractBootAdapter implements NrpcRunner {

    private static final String DEFAULT_LOCAL_SERVICE_CACHE_KEY = "service";

    private static final Logger LOGGER = Logger.getLogger(ServiceExportRunner.class);

    @Override
    @SuppressWarnings("unchecked")
    protected void doStart() {
        NrpcBootstrap instance = NrpcBootstrap.getInstance();
        List<ServiceConfig> serviceConfigs = instance.getServiceConfigs();
        if (serviceConfigs.isEmpty()) {
            LOGGER.info(MessageManager.get("no.service"));
            return;
        }
        String localIp = System.getProperty("local.ip");
        ApplicationConfig applicationConfig = NrpcBootstrap.getInstance().getApplicationConfig();
        List<ProtocolConfig> protocolConfigs = applicationConfig.getProtocolConfigs();
        //FIXME 暂时按照应用级别的协议配置优先，当存在对应协议的依赖时，会用所有协议暴露服务
        for (ProtocolConfig config : protocolConfigs) {
            String protocolName = config.getName();
            Map<String, String> params = config.getParameters();
            URL url = new URL(protocolName, localIp, config.getPort());
            url.setParameters(params);
            Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(protocolName);
            Server export = protocol.export(url);
            export.initialize();
            export.start();
        }

        //缓存本地服务
        LocalCache<String, NrpcService> serviceConfigCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension(DEFAULT_LOCAL_SERVICE_CACHE_KEY);
        for (ServiceConfig serviceConfig : serviceConfigs) {
            if (serviceConfig.getProtocolConfigs() == null || serviceConfig.getProtocolConfigs().isEmpty()) {
                serviceConfig.setProtocolConfigs(protocolConfigs);
            }
            NrpcService nrpcService = new NrpcService();
            nrpcService.setServiceInterface(serviceConfig.getServiceInterface());
            nrpcService.setName(serviceConfig.getName());
            nrpcService.setRef(serviceConfig.getReference());
            nrpcService.setVersion(serviceConfig.getVersion());
            serviceConfigCache.set(serviceConfig.getName(), nrpcService);
        }
    }

    @Override
    public int getPriority() {
        return SERVICE_EXPORT_PRIORITY;
    }
}
