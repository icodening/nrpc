package cn.icodening.nrpc.invoker.cluster;

import cn.icodening.nrpc.invoker.Invoker;
import cn.icodening.rpc.core.exchange.Request;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 故障转移
 *
 * @author icodening
 * @date 2021.04.04
 */
public class FailoverCluster extends AbstractCluster {

    public static final String NAME = "failover";

    private static final Logger LOGGER = Logger.getLogger(FailoverCluster.class);

    public FailoverCluster(List<Invoker> invokers) {
        super(invokers);
    }

    @Override
    protected void doInvoke(List<Invoker> invokers, Request request) {
        boolean isDone = false;
        List<Invoker> copyInvokers = new ArrayList<>(invokers);
        for (int i = 0; i < 2; i++) {
            Invoker invoker = select(copyInvokers);
            final Invoker currentInvoker = invoker;
            try {
                invoker.invoke(request);
                isDone = true;
            } catch (Exception e) {
                //FIXME
                copyInvokers.removeIf(x -> x.getRemoteUrl().equals(currentInvoker.getRemoteUrl()));
            }
            if (isDone) {
                return;
            }
        }

    }
}
