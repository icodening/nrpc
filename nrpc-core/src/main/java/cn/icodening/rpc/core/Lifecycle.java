package cn.icodening.rpc.core;

/**
 * 基本组件的生命周期
 *
 * @author icodening
 * @date 2020.12.26
 */
public interface Lifecycle {

    /**
     * 启动
     */
    void start();

    /**
     * 销毁
     */
    void destroy();
}
