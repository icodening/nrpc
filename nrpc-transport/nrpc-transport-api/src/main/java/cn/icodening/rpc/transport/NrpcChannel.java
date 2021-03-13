package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.Node;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.task.ListenableFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2021.01.24
 */
public interface NrpcChannel extends Node {

    InetSocketAddress getRemoteAddress();

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    void call(ExchangeMessage exchangeMessage);

    <T> ListenableFuture<T> callWithListenableFuture(ExchangeMessage exchangeMessage);

    <T> ListenableFuture<T> callWithListenableFuture(ExchangeMessage exchangeMessage, Executor executor);
}
