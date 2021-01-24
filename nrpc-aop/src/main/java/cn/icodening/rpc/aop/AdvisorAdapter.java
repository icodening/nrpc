package cn.icodening.rpc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * 通知适配
 *
 * @author icodening
 * @date 2021.01.09
 */
public interface AdvisorAdapter {

    /**
     * 是否支持此advice
     *
     * @param advice 通知
     * @return true支持，false不支持
     */
    boolean support(Advice advice);

    /**
     * 获得一个拦截器
     *
     * @param advisor 通知持有者
     * @return 对应的方法拦截器
     */
    MethodInterceptor getInterceptor(Advisor advisor);
}
