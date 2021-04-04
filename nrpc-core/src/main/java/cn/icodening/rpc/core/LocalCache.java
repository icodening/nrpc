package cn.icodening.rpc.core;

import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.15
 */
@Extensible
public interface LocalCache<K, V> {

    V get(K name);

    V set(K name, V v);

    void remove(K name);

    void clear();
}
