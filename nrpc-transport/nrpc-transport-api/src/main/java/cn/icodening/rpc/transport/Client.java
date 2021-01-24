package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.Lifecycle;
import cn.icodening.rpc.core.Node;

import java.net.InetSocketAddress;

/**
 * @author icodening
 * @date 2021.01.24
 */
public interface Client extends Lifecycle, Node {

    ChannelHandler getChannelHandler();

    InetSocketAddress getLocalAddress();
}
