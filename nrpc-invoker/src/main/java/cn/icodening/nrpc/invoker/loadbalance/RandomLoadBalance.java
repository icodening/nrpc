package cn.icodening.nrpc.invoker.loadbalance;

import cn.icodening.nrpc.invoker.Invoker;
import cn.icodening.nrpc.invoker.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author icodening
 * @date 2021.03.24
 */
public class RandomLoadBalance implements LoadBalance {
    public static final String NAME = "random";

    @Override
    public Invoker select(List<Invoker> invokers) {
        if (invokers == null || invokers.isEmpty()) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        Random random = new Random();
        int index = random.nextInt(invokers.size());
        return invokers.get(index);
    }
}
