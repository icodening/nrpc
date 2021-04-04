package cn.icodening.nrpc.invoker.advisor;

import cn.icodening.nrpc.invoker.ClientDelegateInvoker;
import cn.icodening.nrpc.invoker.ClientRequestInterceptor;
import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.aop.MethodBeforeAdvice;
import cn.icodening.rpc.core.exchange.ExchangeMessage;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author icodening
 * @date 2021.04.03
 */
public class ClientRequestInterceptorAdvisor implements MethodBeforeAdvice, Advisor {

    private final List<ClientRequestInterceptor> requestInterceptors;

    public ClientRequestInterceptorAdvisor() {
        List<ClientRequestInterceptor> requestInterceptors = ExtensionLoader.getExtensionLoader(ClientRequestInterceptor.class).getAllExtension();
        if (requestInterceptors == null) {
            requestInterceptors = Collections.emptyList();
        }
        requestInterceptors.sort(null);
        this.requestInterceptors = requestInterceptors;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (!(args[0] instanceof Request)) {
            return;
        }
        for (ClientRequestInterceptor requestInterceptor : requestInterceptors) {
            Request arg = (Request) args[0];
            requestInterceptor.intercept(arg);
        }
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    @Override
    public boolean matchMethod(Method method, Class<?> targetClass) {
        return ("invoke".equals(method.getName())
                && ClientDelegateInvoker.class.isAssignableFrom(targetClass)
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == ExchangeMessage.class);
    }
}
