package cn.icodening.rpc.common;

import cn.icodening.rpc.common.model.NrpcService;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardResponse;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.ResponseFuture;
import cn.icodening.rpc.transport.Client;
import cn.icodening.rpc.transport.NrpcChannelHandler;
import cn.icodening.rpc.transport.Server;
import cn.icodening.rpc.transport.TransportFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.04.02
 */
public abstract class AbstractProtocol implements Protocol {

    private final Map<String, Server> serverMap = new ConcurrentHashMap<>();

    private final NrpcChannelHandler serverChannelHandler = (nrpcChannel, message) -> {
        String targetClazzName = message.getHeader("targetClazz");
        String methodName = message.getHeader("method");
        Object data = message.getData();
        List<Class<?>> classes = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        if (data instanceof JSONArray) {
            JSONArray d = (JSONArray) data;
            for (int i = 0; i < d.size(); i++) {
                JSONObject o = d.getJSONObject(i);
                o.forEach((k, v) -> {
                    try {
                        classes.add(Class.forName(k));
                        objs.add(v);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        LocalCache<String, NrpcService> service = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("service");
        NrpcService nrpcService = service.get(targetClazzName);
        try {
            Method method = nrpcService.getServiceInterface().getMethod(methodName, classes.toArray(new Class[0]));
            Object invoke = method.invoke(nrpcService.getRef(), objs.toArray());
            StandardResponse standardResponse = new StandardResponse();
            standardResponse.setRequestId(message.getId());
            standardResponse.setResult(invoke);
            standardResponse.setHeaders(message.getHeaders());
            nrpcChannel.call(standardResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private final NrpcChannelHandler clientChannelHandler = (nrpcChannel, message) -> {
        LocalCache<Long, ResponseFuture> futureCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("responseFutureCache");
        long requestId = ((Response) message).getRequestId();
        ResponseFuture responseFuture = futureCache.get(requestId);
        responseFuture.setResponse((Response) message);
    };

    @Override
    public Client refer(URL url) {
        String clientType = url.getParameter("client", "netty4");
        TransportFactory transportFactory = ExtensionLoader.getExtensionLoader(TransportFactory.class).getExtension(clientType);
        return transportFactory.createClient(url, getClientCodec(), getClientHandler());
    }


    @Override
    public Server export(URL url) {
        String serverType = url.getParameter("server", "netty4");
        Server server = serverMap.get(serverType);
        if (server == null) {
            synchronized (serverType.intern()) {
                if (url.getPort() == null || url.getPort() == 0) {
                    url.setPort(defaultPort());
                }
                server = serverMap.get(serverType);
                if (server == null) {
                    server = createServer(url);
                    serverMap.putIfAbsent(serverType, server);
                }
            }
        }
        return server;
    }

    protected Server createServer(URL url) {
        String serverType = url.getParameter("server", "netty4");
        TransportFactory transportFactory = ExtensionLoader.getExtensionLoader(TransportFactory.class).getExtension(serverType);
        return transportFactory.createServer(url, getServerCodec(), getServerHandler());
    }

    protected NrpcChannelHandler getServerHandler() {
        return serverChannelHandler;
    }

    protected NrpcChannelHandler getClientHandler() {
        return clientChannelHandler;
    }

}
