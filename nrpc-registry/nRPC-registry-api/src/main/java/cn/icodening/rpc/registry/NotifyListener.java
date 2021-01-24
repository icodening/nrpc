package cn.icodening.rpc.registry;

import cn.icodening.rpc.core.URL;

import java.util.List;

/**
 * @author icodening
 * @date 2020.12.28
 */
public interface NotifyListener {

    void onNotify(List<URL> urls);
}
