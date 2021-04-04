package cn.icodening.nrpc.invoker.cluster;

import cn.icodening.nrpc.invoker.Invoker;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 快速失败
 *
 * @author icodening
 * @date 2021.03.21
 */
public class FailFastCluster extends AbstractCluster {

    public static final String NAME = "failfast";

    private static final Logger LOGGER = Logger.getLogger(ExtensionLoader.class);

    public FailFastCluster(List<Invoker> invokers) {
        super(invokers);
    }

    @Override
    protected void doInvoke(List<Invoker> invokers, Request request) {
        try {
            Invoker invoker = select(invokers);
            invoker.invoke(request);
        } catch (Exception e) {
            //FIXME
            LOGGER.error(NAME, e);
        }
    }
}
