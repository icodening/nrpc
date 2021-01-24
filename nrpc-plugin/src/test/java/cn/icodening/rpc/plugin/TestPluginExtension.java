package cn.icodening.rpc.plugin;

import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.01.20
 */
@Extensible
public interface TestPluginExtension {

    void handlerString(String string);

    String getNewString(String string);
}
