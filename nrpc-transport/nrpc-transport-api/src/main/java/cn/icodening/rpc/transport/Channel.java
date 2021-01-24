package cn.icodening.rpc.transport;

import java.net.InetSocketAddress;

/**
 * @author icodening
 * @date 2021.01.24
 */
public interface Channel {

    InetSocketAddress getRemoteAddress();

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

    void removeAttribute(String key);
}
