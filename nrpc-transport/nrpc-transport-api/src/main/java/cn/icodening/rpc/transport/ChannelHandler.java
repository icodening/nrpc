package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.extension.Extensible;

import java.nio.channels.Channel;

/**
 * @author icodening
 * @date 2021.01.24
 */
@Extensible
public interface ChannelHandler {

    void received(Channel channel,Object message);
}
