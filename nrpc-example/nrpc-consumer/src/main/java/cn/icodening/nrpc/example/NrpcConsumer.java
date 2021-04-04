package cn.icodening.nrpc.example;

import cn.icodening.nrpc.example.api.IEchoService;
import cn.icodening.nrpc.example.model.Student;
import cn.icodening.nrpc.invoker.cluster.FailoverCluster;
import cn.icodening.rpc.config.ApplicationConfig;
import cn.icodening.rpc.config.NrpcBootstrap;
import cn.icodening.rpc.config.ReferenceConfig;
import cn.icodening.rpc.config.RegistryConfig;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.extension.ExtensionLoader;

import java.util.Collections;
import java.util.List;

/**
 * @author icodening
 * @date 2021.04.04
 */
public class NrpcConsumer {

    public static void main(String[] args) throws Exception {
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setInterfaceClass(IEchoService.class);
        referenceConfig.setServiceName(IEchoService.class.getName());
        referenceConfig.setCluster(FailoverCluster.NAME);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setUrl(URL.valueOf("nacos://127.0.0.1:8848"));
        referenceConfig.setRegistryConfigList(Collections.singletonList(registryConfig));

        NrpcBootstrap.getInstance()
                .application(new ApplicationConfig("nrpc-provider"))
                .registry(new RegistryConfig("nacos://127.0.0.1:8848"))
                .reference(referenceConfig)
                .start();

        //获取引用代理
        LocalCache<String, Object> reference = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("reference");

        IEchoService echoService = (IEchoService) reference.get(IEchoService.class.getName());
        Student abby = echoService.getOne("Abby");
        Student xxx = echoService.getOne("xxx");
        List<Student> hanMeimei = echoService.findStudent("HanMeimei");
        System.out.println(abby);
        System.out.println(hanMeimei);
        System.out.println(xxx);
    }
}
