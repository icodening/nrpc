package cn.icodening.rpc.config.cache;

import cn.icodening.rpc.core.AbstractLocalCache;

/**
 * 客户端引用缓存
 * Key: serviceName
 * Value: reference
 * @author icodening
 * @date 2021.04.03
 */
public class ReferenceLocalCache extends AbstractLocalCache<String, Object> {
}
