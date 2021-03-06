package cn.icodening.rpc.transport;

import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardResponse;
import cn.icodening.rpc.core.extension.Extension;
import cn.icodening.rpc.core.extension.Scope;
import cn.icodening.rpc.plugin.async.Async;

/**
 * @author icodening
 * @date 2021.03.10
 */
@Extension(scope = Scope.PROTOTYPE)
public class ExchangeMessageChannelHandler implements NrpcChannelHandler {

    @Override
    @Async
    public void received(NrpcChannel nrpcChannel, ExchangeMessage message) {
        //TODO LOGGER 收到消息
        if (message instanceof Request) {
            handlerRequest(nrpcChannel, (Request) message);
        } else if (message instanceof Response) {
            handlerResponse(nrpcChannel, (Response) message);
        } else {
            throw new NrpcException("NrpcChannelHandler unknown exception");
        }
    }

    protected void handlerResponse(NrpcChannel nrpcChannel, Response message) {
        System.out.println(message);
        //2.响应
        //2.1 获取Response中的requestId
        //2.2 将对应requestId的任务设为完成，并停止超时判断
        //2.3 获取响应返回值，根据场景判断是否需要进行反序列化
    }

    @SuppressWarnings("unchecked")
    protected void handlerRequest(NrpcChannel nrpcChannel, Request message) {
        //1.请求(此时相对的自身是服务提供者，内存中会有相应的提供者配置)
        //1.1 解析参数
        System.out.println(message);
        StandardResponse response = new StandardResponse();
        String service = message.getHeader("service");
        String version = message.getHeader("version");
        String method = message.getHeader("method");
        String paramClass = message.getHeader("paramClass");
        String paramsString = message.getHeader("params");
//        LocalCache<String, NrpcService> serviceCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("service");
//        NrpcService nrpcService = serviceCache.get(service);
//        Class<?>[] cls = new Class[0];
//        if (paramClass != null) {
//            String[] split = paramClass.split(",");
//            cls = new Class[split.length];
//            for (int i = 0; i < split.length; i++) {
//                Class<?> clz = null;
//                try {
//                    clz = Class.forName(split[i]);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                cls[i] = clz;
//            }
//        }
//        Object[] params = new Object[0];
//        if (paramsString != null) {
//            JSONArray jsonArray = JSON.parseArray(paramsString);
//            params = new Object[jsonArray.size()];
//            for (int i = 0; i < jsonArray.size(); i++) {
//                params[i] = jsonArray.get(i);
//            }
//        }
//        Class<?> serviceInterface = nrpcService.getServiceInterface();
//
//        try {
//            Method declaredMethod = serviceInterface.getDeclaredMethod(method, cls);
//            Object invoke = declaredMethod.invoke(nrpcService.getRef(), params);
//            response.setData(invoke);
//            response.setRequestId(message.getId());
//            nrpcChannel.call(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //需要一个基于SPI机制的Request参数解析器
        //1.2 从参数中获取服务名、服务版本、服务分组
        //1.3 根据服务、版本、分组从map中找到对应的ProviderConfig
        //需要一个map容器
        //1.4 从ProviderConfig中获得最终被调用方法的Reference
        //必须属性:reference->具体方法, target:对象实例, parameterClass[]: 参数列表
        //1.5 将方法调用的返回值设到response中
        //1.6 如果调用方法异常，则将异常message写到response中返回
    }
}
