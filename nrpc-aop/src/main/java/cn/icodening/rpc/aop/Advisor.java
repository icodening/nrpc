package cn.icodening.rpc.aop;

import cn.icodening.rpc.core.extension.Extensible;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2021.01.09
 */
@Extensible
public interface Advisor {

    /**
     * 获得增强器
     *
     * @return 返回一个增强通知
     */
    Advice getAdvice();


    /**
     * 匹配方法
     * @param method 方法
     * @param targetClass 目标的class
     * @return true表示需要增强，false不需要
     */
    boolean matchMethod(Method method, Class<?> targetClass);
}
