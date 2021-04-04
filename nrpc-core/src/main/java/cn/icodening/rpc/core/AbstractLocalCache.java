package cn.icodening.rpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.03.15
 */
public class AbstractLocalCache<K, V> implements LocalCache<K, V> {

    protected final Map<K, V> dataMap = new ConcurrentHashMap<>();

    @Override
    public V get(K name) {
        return dataMap.get(name);
    }

    @Override
    public V set(K name, V v) {
        return dataMap.put(name, v);
    }

    @Override
    public void remove(K name) {
        dataMap.remove(name);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }
}
