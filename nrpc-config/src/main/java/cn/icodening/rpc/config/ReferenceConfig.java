package cn.icodening.rpc.config;

import cn.icodening.nrpc.invoker.cluster.FailFastCluster;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author icodening
 * @date 2021.03.13
 */
public class ReferenceConfig extends AbstractConfig {

    private String serviceName;

    private String cluster = FailFastCluster.NAME;

    private Class<?> interfaceClass;

    private List<RegistryConfig> registryConfigList = new ArrayList<>(4);

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<RegistryConfig> getRegistryConfigList() {
        return registryConfigList;
    }

    public void setRegistryConfigList(List<RegistryConfig> registryConfigList) {
        this.registryConfigList = registryConfigList;
    }

    public void addRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfigList.add(registryConfig);
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
