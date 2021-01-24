package cn.icodening.rpc.registry;

import cn.icodening.rpc.core.URL;
import cn.icodening.rpc.core.util.ConcurrentHashSet;
import cn.icodening.rpc.plugin.time.PrintTime;

import java.util.List;
import java.util.Set;

/**
 * @author icodening
 * @date 2020.12.29
 */
public abstract class AbstractRegistry implements Registry {

    private final URL url;

    private final Set<URL> registered = new ConcurrentHashSet<>();

    protected AbstractRegistry(URL url) {
        this.url = url;
    }

    protected abstract void doUnregister(URL url);

    protected abstract void doRegister(URL url);

    @Override
    @PrintTime
    public void register(URL url) {
        if (registered.add(url)) {
            doRegister(url);
        }
    }

    @Override
    public void unregister(URL url) {
        if (registered.remove(url)) {
            doUnregister(url);
        }
    }

    @Override
    public void subscribe(URL url, NotifyListener notifyListener) {

    }

    @Override
    public void unsubscribe(URL url, NotifyListener notifyListener) {

    }

    @Override
    public List<URL> lookup(URL url) {
        return null;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }


}
