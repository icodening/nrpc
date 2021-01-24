package cn.icodening.rpc.registry;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.extension.Extensible;


/**
 * @author icodening
 * @date 2020.12.28
 */
@Extensible("nacos")
public interface RegistryFactory {

    /**
     * 获得一个注册中心
     *
     * @param url 注册中心连接url
     * @return 注册中心实例
     */
    Registry getRegistry(URL url);

}
