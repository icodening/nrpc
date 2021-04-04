package cn.icodening.rpc.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2021.03.15
 */
public abstract class AbstractConfig {

    private String version;

    private String group;

    private List<ProtocolConfig> protocolConfigs = new ArrayList<>(2);

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<ProtocolConfig> getProtocolConfigs() {
        return protocolConfigs;
    }

    public void setProtocolConfigs(List<ProtocolConfig> protocolConfigs) {
        this.protocolConfigs = protocolConfigs;
    }
}
