package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.concurrent.ListenableFuture;
import cn.icodening.rpc.plugin.extension.Extension;
import cn.icodening.rpc.plugin.extension.ExtensionImpl;
import org.junit.Test;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class AsyncTest {

    @Test
    public void asyncTest() throws InterruptedException {
        Extension extension = ExtensionLoader.getExtensionLoader(Extension.class).getExtension(ExtensionImpl.class);
        Extension extension2 = ExtensionLoader.getExtensionLoader(Extension.class).getExtension(ExtensionImpl.class);
        long begin = System.currentTimeMillis();
        extension.say("Hello world");
        extension.say("Hello world");
        extension.say("Hello world");
        ListenableFuture<String> asyncResult = extension.getString("Hello world");
        asyncResult.addSuccessCallback((value) -> System.out.println(Thread.currentThread().getName() + " 开始回调: " + value));
        asyncResult.addSuccessCallback((value) -> System.out.println(Thread.currentThread().getName() + " 开始回调2222: " + value));
        asyncResult.addFailureCallback((ex) -> System.out.println(Thread.currentThread().getName() + " 异常回调: " + ex.getMessage()));
        System.out.println("耗时：" + (System.currentTimeMillis() - begin) + " ms");
        Thread.sleep(5000);
    }
}
