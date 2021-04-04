package cn.icodening.rpc.config.runner;

import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.config.ApplicationConfig;
import cn.icodening.rpc.config.NrpcBootstrap;
import cn.icodening.rpc.config.NrpcRunner;
import cn.icodening.rpc.config.ProtocolConfig;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.MessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化应用级别配置信息
 *
 * @author icodening
 * @date 2021.04.04
 */
public class ApplicationConfigurationRunner extends AbstractBootAdapter implements NrpcRunner {

    public static final String DEFAULT_APPLICATION_NAME = "nrpc-application";

    @Override
    protected void doStart() {
        NrpcBootstrap bootstrap = NrpcBootstrap.getInstance();
        ApplicationConfig applicationConfig = bootstrap.getApplicationConfig();
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig(DEFAULT_APPLICATION_NAME);
        }
        List<ProtocolConfig> protocolConfigs = applicationConfig.getProtocolConfigs();
        if (protocolConfigs == null
                || protocolConfigs.isEmpty()) {
            List<Protocol> protocols = ExtensionLoader.getExtensionLoader(Protocol.class).getAllExtension();
            if (protocols.isEmpty()) {
                throw new NrpcException(MessageManager.get("no.available.protocol"));
            }
            protocolConfigs = new ArrayList<>();
            for (Protocol protocol : protocols) {
                ProtocolConfig protocolConfig = new ProtocolConfig();
                protocolConfig.setName(protocol.getProtocolName());
                protocolConfig.setPort(protocol.defaultPort());
                protocolConfigs.add(protocolConfig);
            }
            applicationConfig.setProtocolConfigs(protocolConfigs);
        }
        bootstrap.application(applicationConfig);
    }

    @Override
    public int getPriority() {
        return APPLICATION_CONFIG_PRIORITY;
    }
}
