package cn.icodening.rpc.plugin.boot;

import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.event.EventPublisher;
import cn.icodening.rpc.core.event.NrpcEventListener;
import cn.icodening.rpc.core.event.Subscribe;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.plugin.lifecycle.NrpcShutdownCallable;
import cn.icodening.rpc.plugin.lifecycle.NrpcShutdownHook;
import cn.icodening.rpc.plugin.lifecycle.ShutdownEvent;
import org.junit.Test;

import java.util.Date;

/**
 * @author icodening
 * @date 2021.01.22
 */
public class SmartDestroyBootTest {

    @Test
    public void smartDestroyTest() {
        Runtime.getRuntime().addShutdownHook(NrpcShutdownHook.getInstance());
        Boot extension = ExtensionLoader.getExtensionLoader(Boot.class).getExtension("bootTest");
        EventPublisher publisher = ExtensionLoader.getExtensionLoader(EventPublisher.class).getExtension();
        publisher.register(new NrpcEventListener<ShutdownEvent>() {
            @Override
            @Subscribe
            public void onEvent(ShutdownEvent event) {
                System.out.println("当前时间：" + new Date(event.getTimestamp()));
                System.out.println("停止一些东西 .....");
            }
        });
        NrpcShutdownHook.getInstance().addShutdownCallable(new NrpcShutdownCallable() {
            @Override
            public void onShutdown() {
                System.out.println("虚拟机关闭");
            }
        });
        extension.start();
    }
}
