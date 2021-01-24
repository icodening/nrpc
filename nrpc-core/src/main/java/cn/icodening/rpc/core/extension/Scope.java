package cn.icodening.rpc.core.extension;

import cn.icodening.rpc.core.ObjectFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author icodening
 * @date 2021.01.01
 */
@Extensible("singleton")
public interface Scope {

    String SINGLETON = "singleton";

    String PROTOTYPE = "prototype";

    /**
     * 获取实例
     *
     * @param clz           实例的class
     * @param objectFactory 实例的构造工厂
     * @return 实例
     */
    Object getObject(Class<?> clz, ObjectFactory<?> objectFactory);

}
