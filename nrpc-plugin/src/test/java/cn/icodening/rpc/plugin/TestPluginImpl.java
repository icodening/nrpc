package cn.icodening.rpc.plugin;

import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.extension.Extension;
import cn.icodening.rpc.core.extension.Scope;
import cn.icodening.rpc.plugin.time.PrintTime;

import java.util.Random;

/**
 * @author icodening
 * @date 2021.01.20
 */
@Extension(scope = Scope.SINGLETON)
public class TestPluginImpl implements TestPluginExtension, Initializer {

    @Override
    public void initialize() {
        System.out.println(TestPluginImpl.class.getSimpleName() + " 初始化");
    }

    @Override
    @PrintTime
    public void handlerString(String string) {
        int random = new Random().nextInt(500);
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ", " + string);
    }

    @Override
    public String getNewString(String string) {
        try {
            Thread.sleep(new Random().nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "new string = " + string;
    }
}
