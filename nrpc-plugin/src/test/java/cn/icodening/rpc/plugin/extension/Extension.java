package cn.icodening.rpc.plugin.extension;

import cn.icodening.rpc.core.extension.Extensible;
import cn.icodening.rpc.core.util.concurrent.ListenableFuture;

/**
 * @author icodening
 * @date 2021.01.23
 */
@Extensible
public interface Extension {

    void say(String string);

    String echo(String string);

    void error(Object arg);

    ListenableFuture<String> getString(String string);
}
