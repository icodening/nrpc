package cn.icodening.rpc.plugin.time;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打印时间注解
 *
 * @author icodening
 * @date 2021.01.03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PrintTime {

    boolean printMethodName() default true;
}
