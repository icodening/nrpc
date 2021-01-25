package cn.icodening.rpc.plugin.extension;


import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.plugin.async.Async;

import java.util.Random;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class ExtensionImpl implements Extension, Initializer {

    @Override
    @Async(executorName = "myExecutor")
    public void say(String string) {
        sleep(500);
        System.out.println(Thread.currentThread().getName() + ",  say: " + string);
    }

    @Override
    public String echo(String string) {
        sleep(500);
        Random random = new Random();
        int i = random.nextInt(500);
        return "echo: " + string;
    }

    @Override
    public void error() {
        throw new NrpcException("test_error");
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        System.out.println("初始化");
    }
}
