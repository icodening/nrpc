package cn.icodening.rpc.plugin.async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步注解，在扩展点上的方法使用该注解后，可异步调用扩展点方法。
 * 暂不支持返回值
 *
 * @author icodening
 * @date 2021.01.23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {

    String executorName() default "";
}
