package cn.icodening.rpc.registry;

import cn.icodening.rpc.core.Node;
import cn.icodening.rpc.core.URL;

import java.util.List;

/**
 * 注册中心
 * FIXME 优化使用方式，将参数URL换成便于理解的通用POJO对象
 *
 * @author icodening
 * @date 2020.12.28
 */
public interface Registry extends Node {

    /**
     * 注册服务
     *
     * @param url 服务URL，包含ip、port、param
     */
    void register(URL url);

    /**
     * 取消注册
     *
     * @param url 服务URL，包含ip、port、param
     */
    void unregister(URL url);

    /**
     * 订阅服务列表
     *
     * @param url            需要订阅的服务url
     * @param notifyListener 服务列表改变后处理
     */
    void subscribe(URL url, NotifyListener notifyListener);

    /**
     * 取消订阅
     *
     * @param url            需要取消的服务url
     * @param notifyListener 服务列表改变后处理
     */
    void unsubscribe(URL url, NotifyListener notifyListener);

    /**
     * 获取服务列表
     *
     * @param url
     * @return
     */
    List<URL> lookup(URL url);
}
