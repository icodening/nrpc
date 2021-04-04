package cn.icodening.nrpc.invoker;

import cn.icodening.nrpc.invoker.loadbalance.RandomLoadBalance;
import cn.icodening.rpc.core.extension.Extensible;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.21
 */
@Extensible(RandomLoadBalance.NAME)
public interface LoadBalance {

    Invoker select(List<Invoker> invokers);
}
