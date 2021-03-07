package cn.icodening.rpc.plugin.extension;


import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.task.ListenableFuture;
import cn.icodening.rpc.core.util.MessageManager;
import cn.icodening.rpc.plugin.async.Async;
import cn.icodening.rpc.plugin.async.AsyncResult;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class ExtensionImpl implements Extension, Initializer {

    @PostConstruct
    public void init1() {
        System.out.println("init 1");
    }

    @PostConstruct
    public void init2() {
        System.out.println("init 2");
    }


    @Override
    @Async
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
    public void error(Object arg) {
        throw new NrpcException(MessageManager.get("test_error", arg));
    }

    @Override
    @Async(executorName = "myExecutor")
    public ListenableFuture<String> getString(String string) {
        String result = "async : " + string;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(1/0);
        return new AsyncResult<>(result);
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
