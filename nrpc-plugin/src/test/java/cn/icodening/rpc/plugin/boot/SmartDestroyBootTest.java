package cn.icodening.rpc.plugin.boot;

import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import org.junit.Test;

/**
 * @author icodening
 * @date 2021.01.22
 */
public class SmartDestroyBootTest {

    @Test
    public void smartDestroyTest(){
        Boot extension = ExtensionLoader.getExtensionLoader(Boot.class).getExtension("bootTest");
        extension.start();
    }
}
