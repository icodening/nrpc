package cn.icodening.rpc.registry.nacos;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.util.ReflectUtil;
import cn.icodening.rpc.plugin.time.PrintTime;
import cn.icodening.rpc.registry.AbstractRegistryFactory;
import cn.icodening.rpc.registry.Registry;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alibaba.nacos.api.PropertyKeyConst.SERVER_ADDR;
import static java.lang.reflect.Modifier.*;

/**
 * @author icodening
 * @date 2020.12.29
 */
public class NacosRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistryService(URL url) {
        Properties properties = new Properties();
        String serverAddress = url.getHost() + ":" + url.getPort();
        properties.put(SERVER_ADDR, serverAddress);
        Set<String> collect = Stream.of(PropertyKeyConst.class.getFields())
                .filter(f -> isStatic(f.getModifiers())
                        && isPublic(f.getModifiers())
                        && isFinal(f.getModifiers())
                        && String.class.equals(f.getType()))
                .map(f -> ReflectUtil.getFieldValue(null, f))
                .map(String.class::cast)
                .collect(Collectors.toSet());
        Map<String, String> parameters = url.getParameters(collect);
        properties.putAll(parameters);
        NamingService namingService = null;
        try {
            namingService = NacosFactory.createNamingService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return new NacosRegistry(url, namingService);
    }
}
