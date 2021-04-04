package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.extension.Extensible;

import java.util.List;

/**
 * @author icodening
 * @date 2021.04.03
 */
@Extensible("")
public interface ClusterFactory {

    Cluster getCluster(List<Invoker> invokers);
}
