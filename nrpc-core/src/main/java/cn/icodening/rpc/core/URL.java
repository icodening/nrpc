package cn.icodening.rpc.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    public URL() {
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

    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf('?');
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        String key = part.substring(0, j);
                        String value = part.substring(j + 1);
                        parameters.put(key, value);
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf('/');
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.lastIndexOf('@');
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(':');
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.lastIndexOf(':');
        if (i >= 0 && i < url.length() - 1) {
            if (url.lastIndexOf('%') > i) {
                // ipv6 address with scope id
                // e.g. fe80:0:0:0:894:aeec:f37d:23e1%en0
                // see https://howdoesinternetwork.com/2013/ipv6-zone-id
                // ignore
            } else {
                port = Integer.parseInt(url.substring(i + 1));
                url = url.substring(0, i);
            }
        }
        if (url.length() > 0) {
            host = url;
        }
        URL retURL = new URL(protocol, host, port);
        if (parameters != null) {
            retURL.setParameters(parameters);
        }
        return retURL;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        URL url = (URL) o;
//        return Objects.equals(protocol, url.protocol) &&
//                Objects.equals(host, url.host) &&
//                Objects.equals(port, url.port);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(protocol, host, port);
//    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        URL url = (URL) object;
        return Objects.equals(protocol, url.protocol) &&
                Objects.equals(host, url.host) &&
                Objects.equals(port, url.port) &&
                Objects.equals(parameters, url.parameters) &&
                Objects.equals(metaData, url.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, host, port, parameters, metaData);
    }

    @Override
    public String toString() {
        return "URL{" +
                "protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", parameters=" + parameters +
                ", metaData=" + metaData +
                '}';
    }
}
