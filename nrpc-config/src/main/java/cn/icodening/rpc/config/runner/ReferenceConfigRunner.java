package cn.icodening.rpc.config.runner;

import cn.icodening.nrpc.invoker.*;
import cn.icodening.rpc.aop.util.AopUtil;
import cn.icodening.rpc.common.Protocol;
import cn.icodening.rpc.config.NrpcBootstrap;
import cn.icodening.rpc.config.NrpcRunner;
import cn.icodening.rpc.config.ReferenceConfig;
import cn.icodening.rpc.config.RegistryConfig;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.boot.AbstractBootAdapter;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.FileUtil;
import cn.icodening.rpc.registry.NotifyListener;
import cn.icodening.rpc.registry.Registry;
import cn.icodening.rpc.registry.RegistryFactory;
import cn.icodening.rpc.registry.RegistryKeyConstant;
import cn.icodening.rpc.transport.Client;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 消费者配置
 * 先加载本地文件缓存中的配置，再与当前的消费者配置进行合并
 * 加载本地文件首先判断该缓存文件的状态，是正常关闭服务还是异常停机
 * 异常停机：合并服务
 * 正常停机：全量替换
 *
 * @author icodening
 * @date 2021.03.14
 */
public class ReferenceConfigRunner extends AbstractBootAdapter implements NrpcRunner {

    private static final Logger LOGGER = Logger.getLogger(ReferenceConfigRunner.class);

    private static final String REFERENCE_FILE_NAME = "cache/reference_cache.nrpc";

    private static final String REFERENCE_FILE_STATUS_OPEN = "OPEN";
    private static final String REFERENCE_FILE_STATUS_CLOSE = "CLOSE";

    @Override
    @SuppressWarnings("unchecked")
    protected void doStart() {
        //读取缓存
        JSONObject cache = loadCache();
        if (cache != null) {
            String status = cache.getString("status");
            if (REFERENCE_FILE_STATUS_OPEN.equals(status)) {
                //说明是非正常停机，读取消费者配置，后续订阅读到的消费者
                String cacheString = cache.getString("references");
                List<ReferenceConfig> referenceConfigs = JSON.parseArray(cacheString, ReferenceConfig.class);
                List<RegistryConfig> registryConfigs = NrpcBootstrap.getInstance().getRegistryConfigs();
                if (referenceConfigs != null && !referenceConfigs.isEmpty()) {
                    for (ReferenceConfig referenceConfig : referenceConfigs) {
                        for (RegistryConfig registryConfig : registryConfigs) {
                            URL registryConfigUrl = registryConfig.getUrl();
                            RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryConfigUrl.getProtocol());
                            Registry registry = factory.getRegistry(registryConfigUrl);
                            URL url = new URL();
                            url.addParameter(RegistryKeyConstant.SERVICE, referenceConfig.getServiceName());
                            registry.subscribe(url, new NotifyListener() {
                                @Override
                                public void onNotify(List<URL> urls) {
                                    System.out.println("ReferenceConfigRunner notify");
                                    for (URL url1 : urls) {
                                        System.out.println(url1);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        //合并配置信息
        //TODO
        List<ReferenceConfig> referenceConfigs = NrpcBootstrap.getInstance().getReferenceConfigs();
        if (referenceConfigs == null || referenceConfigs.isEmpty()) {
            return;
        }
        for (ReferenceConfig referenceConfig : referenceConfigs) {
            List<RegistryConfig> registryConfigList = referenceConfig.getRegistryConfigList();
            for (RegistryConfig registryConfig : registryConfigList) {
                String registryProtocol = registryConfig.getUrl().getProtocol();
                RegistryFactory factory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registryProtocol);
                Registry registry = factory.getRegistry(registryConfig.getUrl());
                //TODO
                URL url = URL.valueOf("consumer://0.0.0.0?service=" + referenceConfig.getServiceName());

                //得到提供者的地址
                List<URL> lookup = registry.lookup(url);
                ClusterFactory clusterFactory = ExtensionLoader.getExtensionLoader(ClusterFactory.class).getExtension(referenceConfig.getCluster());
                LocalCache<String, Object> invokerProxyCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("reference");
                List<Invoker> invokers = new ArrayList<>();
                Cluster cluster = clusterFactory.getCluster(invokers);
                LocalCache<String, List<URL>> providerUrls = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("providerUrl");
                providerUrls.set(referenceConfig.getInterfaceClass().getName(), lookup);

                ConcurrentMap<URL, Invoker> urlInvokerConcurrentHashMap = new ConcurrentHashMap<>();
                for (URL provider : lookup) {
                    Invoker client = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension("client");
                    urlInvokerConcurrentHashMap.put(provider, client);
                    Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(provider.getProtocol());
                    Client refer = protocol.refer(provider);
                    try {
                        client.setRemoteUrl(provider);
                        Method setClient = ClientDelegateInvoker.class.getMethod("setClient", Client.class);
                        setClient.invoke(AopUtil.getProxyTarget(client), refer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refer.initialize();
                    refer.start();
                    invokers.add(client);
                }
                Class<?> interfaceClass = referenceConfig.getInterfaceClass();
                Object proxyInvoker = InvokerProxyFactory.getProxyInvoker(cluster, new Class[]{interfaceClass});
                invokerProxyCache.set(interfaceClass.getName(), proxyInvoker);
                //TODO fix
                registry.subscribe(url, new NotifyListener() {
                    @Override
                    public void onNotify(List<URL> urls) {
                        //获取到新的列表后，关闭列表中不存在的连接
                        List<URL> lastProviderUrls = providerUrls.get(referenceConfig.getInterfaceClass().getName());
                        if (urls.isEmpty() && lastProviderUrls.isEmpty()) {
                            return;
                        }
                        List<URL> newUrls = new ArrayList<>(lastProviderUrls);
                        if (lastProviderUrls.size() > urls.size()) {
                            //有实例下线
                            LOGGER.info("有实例下线");
                            List<Invoker> lastInvokers = new ArrayList<>(cluster.getInvokers());
                            Set<URL> collect = new HashSet<>(urls);
                            for (URL last : lastProviderUrls) {
                                if (!collect.contains(last)) {
                                    newUrls.removeIf(x -> x.equals(last));
                                    lastInvokers.removeIf(x -> x.getRemoteUrl().equals(last));
                                    Invoker invoker = urlInvokerConcurrentHashMap.get(last);
                                    try {
                                        if (invoker != null) {
                                            invoker.close();
                                        }
                                    } catch (IOException ignore) {
                                    } finally {
                                        urlInvokerConcurrentHashMap.remove(last);
                                    }
                                }
                            }
                            providerUrls.set(referenceConfig.getInterfaceClass().getName(), newUrls);
                            cluster.setInvokers(lastInvokers);

                        } else if (lastProviderUrls.size() < urls.size()) {
                            //有实例上线
                            LOGGER.info("有实例上线");
                            Set<URL> collect = new HashSet<>(lastProviderUrls);
                            List<Invoker> lastInvokers = new ArrayList<>(cluster.getInvokers());
                            for (URL url : urls) {
                                if (!collect.contains(url)) {
                                    //不存在，上线
                                    newUrls.add(url);
                                    Invoker client = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension("client");
                                    urlInvokerConcurrentHashMap.put(url, client);
                                    client.setRemoteUrl(url);
                                    Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(url.getProtocol());
                                    Client refer = protocol.refer(url);
                                    try {
                                        Method setClient = ClientDelegateInvoker.class.getMethod("setClient", Client.class);
                                        setClient.invoke(AopUtil.getProxyTarget(client), refer);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    refer.initialize();
                                    refer.start();
                                    lastInvokers.add(client);
                                }
                            }
                            providerUrls.set(referenceConfig.getInterfaceClass().getName(), newUrls);
                            cluster.setInvokers(lastInvokers);
                        }
                    }
                });
            }
        }
    }

    private JSONObject loadCache() {
        InputStream inputStream = FileUtil.getInputStream(new File(REFERENCE_FILE_NAME));
        try {
            if (inputStream == null) {
                return null;
            }
            return JSON.parseObject(inputStream, JSONObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPriority() {
        return REFERENCE_PRIORITY;
    }
}
