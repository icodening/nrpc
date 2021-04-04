package cn.icodening.rpc.config;

import cn.icodening.rpc.core.URL;

/**
 * @author icodening
 * @date 2021.03.13
 */
public class RegistryConfig extends AbstractConfig {

    private String address;

    private URL url;

    public RegistryConfig() {
    }

    public RegistryConfig(String address) {
        setAddress(address);
    }

    public String getAddress() {
        return address;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setAddress(String address) {
        if (address == null) {
            return;
        }
        this.url = URL.valueOf(address);
    }
}
