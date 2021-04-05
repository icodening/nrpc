package cn.icodening.nrpc.invoker;

import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.exchange.StandardRequest;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.ResponseFuture;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2021.03.21
 */
public class NrpcInvoker implements InvocationHandler {

    private final Cluster cluster;

    private final LocalCache<Long, ResponseFuture> futureCache;

    public NrpcInvoker(Cluster cluster) {
        this.cluster = cluster;
        futureCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("responseFutureCache");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        StandardRequest request = new StandardRequest();

        Class<?> targetClazz = method.getDeclaringClass();
        String methodName = method.getName();

        request.addHeader("targetClazz", targetClazz.getName());
        request.addHeader("method", methodName);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Map<String, Object> argMap = new HashMap<>(2);
            String key = parameterTypes[i].getName();
            Object value = args[i];
            argMap.put(key, value);
            argsList.add(argMap);
        }
        request.setData(argsList);
        cluster.invoke(request);
        ResponseFuture responseFuture = futureCache.get(request.getId());
        if (responseFuture == null) {
            return null;
        }
        Object result;
        try {
            //FIXME 当服务器突然断开后，此处会被阻塞直到超时
            Response response = responseFuture.get(5, TimeUnit.SECONDS);
            if (response == null) {
                return null;
            }
            result = response.getResult();
            if (result instanceof JSONObject) {
                return JSON.parseObject(((JSONObject) result).toJSONString(), method.getReturnType());
            }
        } finally {
            futureCache.remove(request.getId());
        }
        return result;
    }
}
