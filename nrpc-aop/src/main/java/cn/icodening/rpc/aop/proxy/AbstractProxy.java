package cn.icodening.rpc.aop.proxy;

import cn.icodening.rpc.aop.Advisor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author icodening
 * @date 2021.01.03
 */
public abstract class AbstractProxy implements AopProxy {

    public static final Set<String> EXCLUDE_METHOD_NAMES
            = new HashSet<>(Arrays.asList("toString", "equals", "hashCode", "getClass", "notify", "notifyAll", "wait", "clone"));
    private AopConfig aopConfig;

    private Object target;

    private List<Advisor> advisors;

    public AbstractProxy() {
    }

    public AbstractProxy(AopConfig aopConfig) {
        this.target = aopConfig.getTarget();
        this.advisors = aopConfig.getAdvisors();
        this.aopConfig = aopConfig;
    }

    public AbstractProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }

    public void setAdvisors(List<Advisor> advisors) {
        this.advisors = advisors;
    }

    public AopConfig getAopConfig() {
        return aopConfig;
    }

    public void setAopConfig(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }
}
