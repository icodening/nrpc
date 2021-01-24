package cn.icodening.rpc.plugin.async;

import cn.icodening.rpc.plugin.AbstractAnnotationAdvisor;
import org.aopalliance.aop.Advice;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

/**
 * @author icodening
 * @date 2021.01.23
 */
public class AsyncAdvisor extends AbstractAnnotationAdvisor {

    private AsyncExecutionInterceptor asyncExecutionInterceptor;

    @Override
    protected Set<Class<? extends Annotation>> containsAnnotations() {
        return Collections.singleton(Async.class);
    }

    @Override
    public Advice getAdvice() {
        if (asyncExecutionInterceptor == null) {
            asyncExecutionInterceptor = new AsyncExecutionInterceptor(getAnnotationMap());
        }
        return asyncExecutionInterceptor;
    }
}
