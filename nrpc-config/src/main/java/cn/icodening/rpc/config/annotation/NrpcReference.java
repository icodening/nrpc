package cn.icodening.rpc.config.annotation;

import java.lang.annotation.*;

/**
 * @author icodening
 * @date 2021.04.04
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface NrpcReference {

}
