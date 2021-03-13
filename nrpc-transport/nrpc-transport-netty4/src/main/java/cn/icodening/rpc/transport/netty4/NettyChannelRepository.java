package cn.icodening.rpc.transport.netty4;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.transport.NrpcChannel;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class NettyChannelRepository {

    private static final Map<Channel, NrpcChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static NrpcChannel getOrCreate(Channel channel, URL url) {
        NrpcChannel nrpcChannel = CHANNEL_MAP.get(channel);
        if (nrpcChannel == null) {
            CHANNEL_MAP.putIfAbsent(channel, new NettyChannel(url, channel));
            nrpcChannel = CHANNEL_MAP.get(channel);
        }
        return nrpcChannel;
    }

    public static void remove(Channel channel) {
        CHANNEL_MAP.remove(channel);
    }

    public static void set(Channel channel, NrpcChannel nrpcChannel) {
        CHANNEL_MAP.put(channel, nrpcChannel);
    }
}
