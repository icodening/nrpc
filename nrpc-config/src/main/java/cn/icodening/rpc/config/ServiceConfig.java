package cn.icodening.rpc.config;

import cn.icodening.rpc.core.util.MultiValueMap;
import cn.icodening.rpc.core.util.SimpleMultiValueMap;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.13
 */
public class ServiceConfig extends AbstractConfig {

    private String name;

    private ApplicationConfig applicationConfig;

    private Class<?> serviceInterface;

    private Object reference;

    private MultiValueMap<String, String> parameters = new SimpleMultiValueMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
        if (serviceInterface != null) {
            setName(serviceInterface.getName());
        }
    }

    public MultiValueMap<String, String> getParameters() {
        return parameters;
    }

    public String getFirstParameter(String name) {
        return parameters.getFirst(name);
    }

    public List<String> getFirstParameters(String name) {
        return parameters.get(name);
    }

    public void setParameters(MultiValueMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }
}
