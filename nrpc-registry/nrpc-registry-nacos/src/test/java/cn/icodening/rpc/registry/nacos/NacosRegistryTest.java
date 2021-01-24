package cn.icodening.rpc.registry.nacos;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.registry.NotifyListener;
import cn.icodening.rpc.registry.RegistryFactory;
import cn.icodening.rpc.registry.Registry;
import cn.icodening.rpc.registry.RegistryKeyConstant;
import org.junit.Test;

import java.util.List;

/**
 * @author icodening
 * @date 2020.12.30
 */
public class NacosRegistryTest {

    /**
     * 服务实例注册
     */
    @Test
    public void register() throws InterruptedException {
        RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension();
        Registry nacosRegistry = factory.getRegistry(new URL("http", "127.0.0.1", 8848));
        URL testURL1 = createTestURL("127.0.0.1", 9090);
        URL testURL2 = new URL("http", "127.0.0.1", 9090);
        testURL2.addParameter(RegistryKeyConstant.SERVICE, "test-service2")
                .addParameter(RegistryKeyConstant.HEALTHY, Boolean.toString(true))
                .addParameter(RegistryKeyConstant.CLUSTER, "DEFAULT")
                .addParameter(RegistryKeyConstant.ENABLED, Boolean.toString(true))
                .addParameter(RegistryKeyConstant.WEIGHT, Double.toString(1))
                .addParameter(RegistryKeyConstant.EPHEMERAL, Boolean.toString(true));
        nacosRegistry.register(testURL1);
        nacosRegistry.register(testURL2);
    }

    /**
     * 服务实例反注册
     */
    @Test
    public void unregister() throws InterruptedException {
        RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension();
        Registry nacosRegistry = factory.getRegistry(new URL("http", "127.0.0.1", 8848));
        URL testURL = createTestURL("127.0.0.1", 9090);
        nacosRegistry.register(testURL);
        Thread.sleep(10000);
        nacosRegistry.unregister(testURL);
    }

    /**
     * 服务列表订阅
     */
    @Test
    public void subscribe() throws InterruptedException, CloneNotSupportedException {
        RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension();
        Registry nacosRegistry = factory.getRegistry(new URL("http", "127.0.0.1", 8848));
        URL testURL = createTestURL("127.0.0.1", 9090);
        nacosRegistry.register(testURL);

        URL url = createTestURL("127.0.0.1", 9090);
        nacosRegistry.subscribe(url, e -> {
            for (URL instanceURL : e) {
                System.out.println(instanceURL.getHost() + ":" + instanceURL.getPort());
            }
            System.out.println("=== end ===");
        });
        URL clone = createTestURL("127.0.0.1", 8080);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            nacosRegistry.unregister(url);
            nacosRegistry.unregister(clone);
            nacosRegistry.unregister(testURL);
        }));
        Thread.sleep(5000);
        nacosRegistry.register(clone);
        Thread.sleep(5000);

    }

    @Test
    public void testSubscribe() {
    }

    @Test
    public void unsubscribe() throws CloneNotSupportedException, InterruptedException {
//        subscribe();
        RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension();
        Registry nacosRegistry = factory.getRegistry(new URL("http", "127.0.0.1", 8848));
        URL testURL = createTestURL("127.0.0.1", 9090);
        nacosRegistry.register(testURL);

        URL url = createTestURL("127.0.0.1", 9090);
        NotifyListener n = e -> {
            for (URL instanceURL : e) {
                System.out.println(instanceURL.getHost() + ":" + instanceURL.getPort());
            }
            System.out.println("=== end ===");
        };
        nacosRegistry.subscribe(url, n);
        URL clone = createTestURL("127.0.0.1", 8080);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            nacosRegistry.unregister(url);
            nacosRegistry.unregister(clone);
            nacosRegistry.unregister(testURL);
        }));
        Thread.sleep(5000);
        nacosRegistry.register(clone);
        Thread.sleep(5000);
        URL clone1 = createTestURL("127.0.0.1", 9090);
        nacosRegistry.unsubscribe(clone1, n);
        Thread.sleep(5000);
        nacosRegistry.unregister(createTestURL("127.0.0.1", 9090));
        Thread.sleep(10000);
    }

    @Test
    public void lookup() throws InterruptedException {
        register();
        RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension();
        Registry nacosRegistry = factory.getRegistry(new URL("http", "127.0.0.1", 8848));
        URL testURL = createTestURL("127.0.0.1", 9090);
        Thread.sleep(5000);
        List<URL> lookup = nacosRegistry.lookup(testURL);
        for (URL url : lookup) {
            System.out.println(url);
        }
        Thread.sleep(10000);
    }


    private URL createTestURL(String host, int port) {
        URL url = new URL("http", host, port);
        url.addParameter(RegistryKeyConstant.SERVICE, "test-service")
                .addParameter(RegistryKeyConstant.HEALTHY, Boolean.toString(true))
                .addParameter(RegistryKeyConstant.CLUSTER, "DEFAULT")
                .addParameter(RegistryKeyConstant.ENABLED, Boolean.toString(true))
                .addParameter(RegistryKeyConstant.WEIGHT, Double.toString(1))
                .addParameter(RegistryKeyConstant.EPHEMERAL, Boolean.toString(true));
        return url;
    }
}