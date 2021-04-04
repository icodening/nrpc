package cn.icodening.nrpc.invoker.cluster;

import cn.icodening.nrpc.invoker.Cluster;
import cn.icodening.nrpc.invoker.Invoker;
import cn.icodening.nrpc.invoker.LoadBalance;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.ExtensionLoader;

import java.util.List;

/**
 * @author icodening
 * @date 2021.04.03
 */
public abstract class AbstractCluster implements Cluster {

    private LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension();

    private volatile List<Invoker> invokers;

    protected boolean available = true;

    protected abstract void doInvoke(List<Invoker> invokers, Request request);

    public AbstractCluster(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public void invoke(ExchangeMessage request) {
        try {
            //TODO add log
            doInvoke(invokers, (Request) request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    @Override
    public List<Invoker> getInvokers() {
        return invokers;
    }

    @Override
    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    @Override
    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance == null ? this.loadBalance : loadBalance;
    }

    protected Invoker select(List<Invoker> invokers) {
        if (invokers == null || invokers.isEmpty()) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return loadBalance.select(invokers);
    }
}
