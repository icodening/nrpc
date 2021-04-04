package cn.icodening.rpc.core.filter;

import cn.icodening.rpc.core.exchange.ExchangeMessage;

/**
 * @author icodening
 * @date 2021.03.09
 */
public interface NrpcFilterChain {

    void filter(ExchangeMessage exchangeMessage);
}
