package cn.icodening.rpc.plugin;

import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.aop.EmptyAnnotation;
import cn.icodening.rpc.aop.proxy.AbstractProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.01.20
 */
public abstract class AbstractAnnotationAdvisor implements Advisor {

    private final Map<Method, Annotation> annotationMap = new ConcurrentHashMap<>();

    public Map<Method, Annotation> getAnnotationMap() {
        return annotationMap;
    }

    /**
     * 存在指定的注解class时，增强对应的方法
     *
     * @return 注解集合
     */
    protected abstract Set<Class<? extends Annotation>> containsAnnotations();

    @SuppressWarnings("unchecked")
    protected <T> T findTargetAnnotation(Annotation[] annotations, Class<T> target) {
        if (annotations.length == 0) {
            return (T) EmptyAnnotation.getInstance();
        }
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(target)) {
                return (T) annotation;
            }
        }
        return (T) EmptyAnnotation.getInstance();
    }

    @Override
    public boolean matchMethod(Method method, Class<?> targetClass) {
        if (AbstractProxy.EXCLUDE_METHOD_NAMES.contains(method.getName())) {
            annotationMap.putIfAbsent(method, EmptyAnnotation.getInstance());
            return false;
        }
        Annotation targetAnnotation = annotationMap.get(method);
        if (targetAnnotation == null) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            Set<Class<? extends Annotation>> classes = containsAnnotations();
            for (Class<? extends Annotation> clz : classes) {
                Annotation found = findTargetAnnotation(annotations, clz);
                annotationMap.putIfAbsent(method, found);
                targetAnnotation = annotationMap.get(method);
            }
        }
        return !(targetAnnotation instanceof EmptyAnnotation);
    }
}
