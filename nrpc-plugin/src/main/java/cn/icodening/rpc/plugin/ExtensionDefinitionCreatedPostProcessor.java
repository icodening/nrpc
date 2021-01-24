package cn.icodening.rpc.plugin;

import cn.icodening.rpc.core.ObjectFactory;
import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.core.extension.ExtensionDefinition;
import cn.icodening.rpc.core.extension.ExtensionLoadedPostProcessor;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.aop.proxy.DefaultAopConfig;
import cn.icodening.rpc.aop.proxy.JdkDynamicProxy;

import java.util.*;

/**
 * @author icodening
 * @date 2021.01.18
 */
public class ExtensionDefinitionCreatedPostProcessor implements ExtensionLoadedPostProcessor {

    private static final List<CreatedExtensionPostProcessor> CREATED_EXTENSION_POST_PROCESSORS =
            ExtensionLoader.getExtensionLoader(CreatedExtensionPostProcessor.class).getAllExtension();

    static {
        Collections.sort(CREATED_EXTENSION_POST_PROCESSORS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void postLoaded(List<ExtensionDefinition<?>> extensionDefinitions, ExtensionLoader<?> extensionLoader) {
        for (ExtensionDefinition<?> extensionDefinition : extensionDefinitions) {
            Set<Advisor> ads = new HashSet<>();
            for (CreatedExtensionPostProcessor createdExtensionPostProcessor : CREATED_EXTENSION_POST_PROCESSORS) {
                if (createdExtensionPostProcessor.supportReturnType(extensionDefinition.getClazz())) {
                    ads.add(createdExtensionPostProcessor);
                }
            }
            if (ads.size() > 0) {
                DefaultAopConfig defaultAopConfig = new DefaultAopConfig(extensionDefinition.getObjectFactory(), new ArrayList<>(ads));
                JdkDynamicProxy jdkDynamicProxy = new JdkDynamicProxy(defaultAopConfig);
                ObjectFactory proxy = (ObjectFactory) jdkDynamicProxy.getProxy();
                extensionDefinition.setObjectFactory(proxy);
            }

        }

    }
}
