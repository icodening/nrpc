package cn.icodening.rpc.core.util;

import java.util.List;
import java.util.Map;

/**
 * @author icodening
 * @date 2021.03.09
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

    V getFirst(K key);

    default V getFirst(K key, V defaultValue) {
        V first = getFirst(key);
        if (first == null) {
            return defaultValue;
        }
        return first;
    }

    void add(K key, V value);

    void addAll(K key, List<? extends V> values);

    void addAll(MultiValueMap<K, V> values);

    default void addIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            add(key, value);
        }
    }

    void set(K key, V value);

    void setAll(Map<K, V> values);

    Map<K, V> toSingleValueMap();

}
