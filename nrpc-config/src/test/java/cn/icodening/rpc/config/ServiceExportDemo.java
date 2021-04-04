package cn.icodening.rpc.config;

import cn.icodening.rpc.config.api.IDemoService;
import cn.icodening.rpc.config.api.IHelloService;
import cn.icodening.rpc.config.impl.DemoServiceImpl;
import cn.icodening.rpc.config.impl.HelloServiceImpl;
import cn.icodening.rpc.core.event.EventPublisher;
import cn.icodening.rpc.core.event.NrpcEventListener;
import cn.icodening.rpc.core.event.Subscribe;
import cn.icodening.rpc.core.extension.ExtensionLoader;

import java.util.Date;

/**
 * @author icodening
 * @date 2021.03.16
 */
public class ServiceExportDemo {

    public static void main(String[] args) {
        EventPublisher eventPublisher = ExtensionLoader.getExtensionLoader(EventPublisher.class).getExtension();
        eventPublisher.register(new NrpcEventListener<NrpcStartedEvent>() {
            @Override
            @Subscribe
            public void onEvent(NrpcStartedEvent event) {
                System.out.println("监听 NRPC启动完成事件: " + new Date(event.getTimestamp()));
            }
        });
        ServiceConfig serviceConfig1 = new ServiceConfig();
        serviceConfig1.setVersion("1.0");
        serviceConfig1.setServiceInterface(IHelloService.class);
        serviceConfig1.setReference(new HelloServiceImpl());
        serviceConfig1.setName(HelloServiceImpl.class.getName());

        ServiceConfig serviceConfig2 = new ServiceConfig();
        serviceConfig2.setVersion("1.0");
        serviceConfig2.setServiceInterface(IDemoService.class);
        serviceConfig2.setReference(new DemoServiceImpl());
        serviceConfig2.setName(DemoServiceImpl.class.getName());

        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setServiceName("demo-service");


        NrpcBootstrap.getInstance()
                .application(new ApplicationConfig("nrpc-app"))
                .registry(new RegistryConfig("nacos://127.0.0.1:8848"))
                .service(serviceConfig1)
                .service(serviceConfig2)
//                .reference(referenceConfig)
                .start();
    }
}
