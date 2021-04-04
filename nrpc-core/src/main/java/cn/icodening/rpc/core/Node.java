package cn.icodening.rpc.core;

/**
 * 基本节点接口
 * 服务器、客户端、注册中心 等都抽象为一个点
 *
 * @author icodening
 * @date 2020.12.30
 */
public interface Node {

    /**
     * 获取URL
     *
     * @return 当前节点自身的URL
     */
    URL getUrl();

    boolean isAvailable();

}
