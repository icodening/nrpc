package cn.icodening.nrpc.invoker.cluster;

import cn.icodening.nrpc.invoker.Cluster;
import cn.icodening.nrpc.invoker.ClusterFactory;
import cn.icodening.nrpc.invoker.Invoker;

import java.util.List;

/**
 * @author icodening
 * @date 2021.04.03
 */
public class FailoverClusterFactory implements ClusterFactory {

    @Override
    public Cluster getCluster(List<Invoker> invokers) {
        return new FailoverCluster(invokers);
    }
}
