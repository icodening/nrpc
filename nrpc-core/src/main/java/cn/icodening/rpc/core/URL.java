package cn.icodening.rpc.core;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author icodening
 * @date 2020.12.28
 */
public class URL implements Serializable {

    private static final String EMPTY_PROTOCOL = "empty";

    private String protocol;

    private String host;

    private Integer port;

    private Map<String, String> parameters = new HashMap<>();

    private Map<String, String> metaData = new HashMap<>();

    public static URL create(String protocol, String host, Integer port) {
        return new URL(protocol, host, port);
    }

    public URL(String host, Integer port) {
        this(EMPTY_PROTOCOL, host, port);
    }

    public URL(String protocol, String host, Integer port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public URL setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public URL setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getParameters(Set<String> set) {
        Map<String, String> map = new HashMap<>();
        for (String key : set) {
            if (parameters.containsKey(key)) {
                map.put(key, parameters.get(key));
            }
        }
        return map;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        if (parameters.get(key) == null) {
            return defaultValue;
        }
        return parameters.get(key);
    }

    public URL setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public URL addParameter(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public URL addMetaData(String key, String value) {
        this.metaData.put(key, value);
        return this;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        URL url = (URL) o;
        return Objects.equals(protocol, url.protocol) &&
                Objects.equals(host, url.host) &&
                Objects.equals(port, url.port) &&
                Objects.equals(parameters, url.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, host, port, parameters);
    }
}
