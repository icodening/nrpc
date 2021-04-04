package cn.icodening.rpc.common.model;

/**
 * @author icodening
 * @date 2021.03.21
 */
public class NrpcService {

    private String name;

    private String version;

    private Object ref;

    private Class<?> serviceInterface;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> interfaceClass) {
        this.serviceInterface = interfaceClass;
    }
}
