package cn.icodening.rpc.config.runner;

import cn.icodening.rpc.config.NrpcBootstrap;
import cn.icodening.rpc.config.NrpcRunner;
import cn.icodening.rpc.config.RegistryConfig;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.util.NetUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * 环境配置初始化
 *
 * @author icodening
 * @date 2021.03.14
 */
public class EnvironmentConfigurationRunner extends AbstractBootAdapter implements NrpcRunner {

    @Override
    protected void doStart() {
        //获取本机可用ip
        String localHostIp = null;
        try (Socket socket = new Socket()) {
            List<RegistryConfig> registryConfigs = NrpcBootstrap.getInstance().getRegistryConfigs();
            for (RegistryConfig registryConfig : registryConfigs) {
                String host = registryConfig.getUrl().getHost();
                int port = registryConfig.getUrl().getPort();
                InetSocketAddress reg = new InetSocketAddress(host, port);
                socket.connect(reg, 1000);
                InetAddress localAddress = socket.getLocalAddress();
                if (!localAddress.isLoopbackAddress()) {
                    localHostIp = localAddress.getHostAddress();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (localHostIp == null) {
            List<String> localhost = NetUtil.getLocalhost();
            if (localhost.size() > 1) {
                //TODO LOGGER WARN
            }
            localHostIp = localhost.get(0);
        }

        System.setProperty("local.ip", localHostIp);
        System.setProperty("local.pid", findPid());
        //TODO 其他配置初始化
        File cache = new File("cache");
        if (!cache.exists()) {
            cache.mkdir();
        }
    }

    private String findPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    @Override
    public int getPriority() {
        return ENV_CONFIG_PRIORITY;
    }
}
