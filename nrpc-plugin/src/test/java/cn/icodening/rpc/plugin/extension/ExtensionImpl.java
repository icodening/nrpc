package cn.icodening.rpc.plugin.extension;


import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.plugin.async.Async;

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
    @Async
    public String echo(String string) {
        sleep(500);
        return "echo: " + string;
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
