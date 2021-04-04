package cn.icodening.rpc.config.cache;

import cn.icodening.rpc.core.AbstractLocalCache;
import cn.icodening.rpc.transport.Server;

import javax.annotation.PreDestroy;

/**
 * @author icodening
 * @date 2021.03.15
 */
public class ServerLocalCache extends AbstractLocalCache<String, Server> {

    @PreDestroy
    public void destroy() {
        for (Server server : dataMap.values()) {
            server.destroy();
        }
    }
}
