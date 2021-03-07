package cn.icodening.rpc.plugin;

import cn.icodening.rpc.aop.Advisor;
import cn.icodening.rpc.aop.AfterReturningAdvice;
import cn.icodening.rpc.core.Sortable;
import cn.icodening.rpc.core.extension.Extensible;
import cn.icodening.rpc.core.util.Holder;
import cn.icodening.rpc.plugin.lifecycle.InitializerDestroyPostProcessor;

import java.lang.reflect.Method;

/**
 * 当一个Extension扩展点被实例化之后的操作
 * 会根据 {@link CreatedExtensionPostProcessor#supportReturnType}的结果来判断是否进行后续操作
 * 符合条件的类将会在被实例化后执行{@link AfterReturningAdvice#afterReturning(Holder, Method, Object[], Object)}中的逻辑
 * <p>
 * 基本默认实现如下
 * {@link InitializerDestroyPostProcessor} 实例化完毕后进行初始化和预销毁
 * {@link ExtensionProxyCreator} 实例化完毕后生成代理对象
 *
 * @author icodening
 * @date 2021.01.15
 */
@Extensible
public interface CreatedExtensionPostProcessor extends Sortable, AfterReturningAdvice, Advisor {

    /**
     * 判断是否支持对应的返回类型
     *
     * @param clazz 返回的Extension类型
     * @return true支持，false不支持
     */
    boolean supportReturnType(Class<?> clazz);
}
