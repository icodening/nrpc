package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * channel处理器
 *
 * @author icodening
 * @date 2021.01.24
 */
@Extensible
public interface NrpcChannelHandler {

    /**
     * 收到 请求/响应 时的动作
     *
     * @param message 请求or响应，Client对应收到响应，Server对应收到请求
     */
    void received(NrpcChannel nrpcChannel, ExchangeMessage message);
}
