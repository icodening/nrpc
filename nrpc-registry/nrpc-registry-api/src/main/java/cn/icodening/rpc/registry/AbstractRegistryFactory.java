package cn.icodening.rpc.registry;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.plugin.time.PrintTime;

import java.util.HashMap;
import java.util.Map;

/**
 * @author icodening
 * @date 2020.12.29
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Map<String, Registry> REGISTRY_SERVICE_MAP = new HashMap<>();

    @Override
    @PrintTime
    public Registry getRegistry(URL url) {
        //先通过缓存获取
        String key = getRegistryServiceCacheKey(url);
        synchronized (key) {
            Registry registryService = REGISTRY_SERVICE_MAP.get(key);
            if (registryService != null) {
                return registryService;
            }
            registryService = createRegistryService(url);
            if (registryService == null) {
                throw new IllegalStateException("Can not create registry " + url);
            }
            REGISTRY_SERVICE_MAP.put(key, registryService);
            return registryService;
        }
    }

    /**
     * 实例化注册服务
     *
     * @param url 注册中心url
     * @return 注册中心
     */
    protected abstract Registry createRegistryService(URL url);

    protected String getRegistryServiceCacheKey(URL url) {
        return url.toString().intern();
    }
}
