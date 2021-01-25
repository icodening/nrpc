package cn.icodening.rpc.registry.nacos;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.registry.AbstractRegistry;
import cn.icodening.rpc.registry.NotifyListener;
import cn.icodening.rpc.registry.RegistryKeyConstant;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nacos注册中心
 *
 * @author icodening
 * @date 2020.12.29
 */
public class NacosRegistry extends AbstractRegistry {

    private static final Logger LOGGER = Logger.getLogger(NacosRegistry.class);

    private final static String DEFAULT_GROUP = "DEFAULT_GROUP";

    private final static String DEFAULT_CLUSTER = "DEFAULT";

    private final NamingService namingService;

    /**
     * 存储nacos服务列表监听器
     * Key: URL
     * Value: 服务列表变化监听器
     */
    private final Map<URL, EventListener> urlEventListenerMap = new ConcurrentHashMap<>();

    public NacosRegistry(URL url, NamingService namingService) {
        super(url);
        this.namingService = namingService;
    }

    @Override
    public void doRegister(URL url) {
        Instance instance = urlToInstance(url);
        String serviceName = url.getParameter(RegistryKeyConstant.SERVICE);
        String groupName = url.getParameter(RegistryKeyConstant.GROUP, DEFAULT_GROUP);
        try {
            namingService.registerInstance(serviceName,
                    groupName,
                    instance);
        } catch (NacosException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void doUnregister(URL url) {
        Instance instance = urlToInstance(url);
        String serviceName = url.getParameter(RegistryKeyConstant.SERVICE);
        String groupName = url.getParameter(RegistryKeyConstant.GROUP, DEFAULT_GROUP);
        try {
            namingService.deregisterInstance(serviceName,
                    groupName,
                    instance);
        } catch (NacosException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void subscribe(URL url, NotifyListener notifyListener) {
        String serviceName = url.getParameters().get(RegistryKeyConstant.SERVICE);
        String groupName = url.getParameter(RegistryKeyConstant.GROUP, DEFAULT_GROUP);
        try {
            namingService.subscribe(serviceName, groupName, getEventListener(url, notifyListener));
        } catch (NacosException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void unsubscribe(URL url, NotifyListener notifyListener) {
        String serviceName = url.getParameters().get(RegistryKeyConstant.SERVICE);
        String groupName = url.getParameter(RegistryKeyConstant.GROUP, DEFAULT_GROUP);
        try {
            namingService.unsubscribe(serviceName, groupName, getEventListener(url, notifyListener));
        } catch (NacosException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        String serviceName = url.getParameters().get(RegistryKeyConstant.SERVICE);
        String groupName = url.getParameter(RegistryKeyConstant.GROUP, DEFAULT_GROUP);
        List<URL> urls = new ArrayList<>();
        try {
            List<Instance> allInstances = namingService.getAllInstances(serviceName, groupName);
            for (Instance instance : allInstances) {
                URL instanceUrl = instanceToUrl(instance);
                urls.add(instanceUrl);
            }
        } catch (NacosException e) {
            LOGGER.error(e);
        }
        return urls;
    }

    private Instance urlToInstance(URL url) {
        Instance instance = new Instance();
        instance.setIp(url.getHost());
        instance.setPort(url.getPort());
        instance.setEnabled(Boolean.parseBoolean(url.getParameter(RegistryKeyConstant.ENABLED, Boolean.toString(true))));
        instance.setEphemeral(Boolean.parseBoolean(url.getParameter(RegistryKeyConstant.EPHEMERAL, Boolean.toString(true))));
        instance.setClusterName(url.getParameter(RegistryKeyConstant.CLUSTER, DEFAULT_CLUSTER));
        instance.setWeight(Double.parseDouble(url.getParameter(RegistryKeyConstant.WEIGHT, "1")));
        instance.setMetadata(new HashMap<>(url.getMetaData()));
        instance.setHealthy(Boolean.parseBoolean(url.getParameter(RegistryKeyConstant.HEALTHY, Boolean.toString(true))));
        return instance;
    }

    private URL instanceToUrl(Instance instance) {
        int port = instance.getPort();
        String instanceServiceName = instance.getServiceName();
        String ip = instance.getIp();
        boolean healthy = instance.isHealthy();
        String clusterName = instance.getClusterName();
        boolean enabled = instance.isEnabled();
        double weight = instance.getWeight();
        boolean ephemeral = instance.isEphemeral();
        Map<String, String> metadata = instance.getMetadata();

        URL instanceUrl = new URL(ip, port);
        instanceUrl.addParameter(RegistryKeyConstant.SERVICE, instanceServiceName)
                .addParameter(RegistryKeyConstant.HEALTHY, Boolean.toString(healthy))
                .addParameter(RegistryKeyConstant.CLUSTER, clusterName)
                .addParameter(RegistryKeyConstant.ENABLED, Boolean.toString(enabled))
                .addParameter(RegistryKeyConstant.WEIGHT, Double.toString(weight))
                .addParameter(RegistryKeyConstant.EPHEMERAL, Boolean.toString(ephemeral));
        instanceUrl.setMetaData(metadata);
        return instanceUrl;
    }

    private EventListener getEventListener(URL url, NotifyListener notifyListener) {
        EventListener eventListener = urlEventListenerMap.get(url);
        if (eventListener == null) {
            EventListener listener = createEventListener(notifyListener);
            urlEventListenerMap.putIfAbsent(url, listener);
            eventListener = urlEventListenerMap.get(url);
        }
        return eventListener;
    }

    private EventListener createEventListener(NotifyListener notifyListener) {
        return event -> {
            List<URL> urls = new ArrayList<>();
            List<Instance> instances = ((NamingEvent) event).getInstances();
            for (Instance instance : instances) {
                URL instanceUrl = instanceToUrl(instance);
                urls.add(instanceUrl);
            }
            notifyListener.onNotify(urls);
        };
    }
}