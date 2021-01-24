package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.Lifecycle;
import cn.icodening.rpc.core.URL;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author icodening
 * @date 2021.01.24
 */
public interface Server extends Lifecycle {

    URL getUrl();

    InetSocketAddress getLocalAddress();

    Collection<Channel> getChannels();

    Channel getChannel(InetSocketAddress remoteAddress);

}
