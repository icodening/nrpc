package cn.icodening.rpc.plugin.time;

import cn.icodening.rpc.plugin.AbstractAnnotationAdvisor;
import org.aopalliance.aop.Advice;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

/**
 * @author icodening
 * @date 2021.01.10
 */
public class PrintTimeAdvisor extends AbstractAnnotationAdvisor {

    private static final Advice PRINT_TIME_INTERCEPTOR = new PrintTimeInterceptor();

    @Override
    public Advice getAdvice() {
        return PRINT_TIME_INTERCEPTOR;
    }

    @Override
    protected Set<Class<? extends Annotation>> containsAnnotations() {
        return Collections.singleton(PrintTime.class);
    }
}
