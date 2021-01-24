package cn.icodening.rpc.core.extension;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author icodening
 * @date 2021.01.01
 */
public class ExtensionLoaderTest {

    @Test
    public void getExtension() throws Exception {
        int max = 100;
        CountDownLatch countDownLatch = new CountDownLatch(max);
        for (int i = 0; i < max; i++) {
            new Thread(() -> {
                countDownLatch.countDown();
                ExtensionLoader<TestExtension> extensionLoader = ExtensionLoader.getExtensionLoader(TestExtension.class);
                TestExtension test = extensionLoader.getExtension(TestExtensionImpl.class);
                test.say("Hello World");
            }).start();
        }
        countDownLatch.await();
        Thread.sleep(2000);
    }
}