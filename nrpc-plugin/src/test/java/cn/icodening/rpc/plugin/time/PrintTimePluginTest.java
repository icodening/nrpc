package cn.icodening.rpc.plugin.time;

import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.plugin.TestPluginExtension;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author icodening
 * @date 2021.01.20
 */
public class PrintTimePluginTest {

    @Test
    public void printTime() throws InterruptedException {
        int max = 50;
        CountDownLatch countDownLatch = new CountDownLatch(max);
        for (int i = 0; i < max; i++) {
            new Thread(() -> {
                countDownLatch.countDown();
                ExtensionLoader<TestPluginExtension> extensionLoader1 = ExtensionLoader.getExtensionLoader(TestPluginExtension.class);
                TestPluginExtension test1 = extensionLoader1.getExtension("test");
                test1.handlerString("hello world");
            }).start();
        }
        countDownLatch.await();
        Thread.sleep(2000);
    }
}
