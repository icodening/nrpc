package cn.icodening.rpc.aop;

import java.lang.annotation.Annotation;

/**
 * @author icodening
 * @date 2021.01.17
 */
public final class EmptyAnnotation implements Annotation {

    private static final EmptyAnnotation EMPTY_ANNOTATION = new EmptyAnnotation();

    private EmptyAnnotation() {
    }

    public static EmptyAnnotation getInstance() {
        return EMPTY_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return EMPTY_ANNOTATION.getClass();
    }
}
