package cn.icodening.rpc.transport.netty4;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.task.ListenableFuture;
import cn.icodening.rpc.core.task.ListenableFutureTask;
import cn.icodening.rpc.transport.NrpcChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2021.03.10
 */
public class NettyChannel implements NrpcChannel {

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private URL url;

    private Channel channel;

    private boolean available = true;


    public NettyChannel(URL url, Channel channel) {
        this.url = url;
        this.channel = channel;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public void call(ExchangeMessage exchangeMessage) {
        ChannelFuture channelFuture = channel.writeAndFlush(exchangeMessage);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //TODO 发送成功日志
            }
            //TODO 发送失败日志
        });
    }

    @Override
    public <T> ListenableFuture<T> callWithListenableFuture(ExchangeMessage exchangeMessage) {
        ListenableFutureTask<T> listenableFutureTask = new ListenableFutureTask<T>(() -> channel.writeAndFlush(exchangeMessage));
        listenableFutureTask.run();
        return listenableFutureTask;
    }

    @Override
    public <T> ListenableFuture<T> callWithListenableFuture(ExchangeMessage exchangeMessage, Executor executor) {
        ListenableFutureTask<T> listenableFutureTask = new ListenableFutureTask<T>(() -> channel.writeAndFlush(exchangeMessage));
        executor.execute(listenableFutureTask);
        return listenableFutureTask;
    }


    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }
}
