package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.exchange.ExchangeMessage;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.21
 */
public interface Cluster {

    void invoke(ExchangeMessage request);

    void setInvokers(List<Invoker> invokers);

    List<Invoker> getInvokers();

    LoadBalance getLoadBalance();

    void setLoadBalance(LoadBalance loadBalance);

}
