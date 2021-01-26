package cn.icodening.rpc.plugin.exception;

import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.plugin.extension.Extension;
import cn.icodening.rpc.plugin.extension.ExtensionImpl;
import org.junit.Test;

/**
 * @author icodening
 * @date 2021.01.25
 */
public class ExceptionHandlerTest {

    @Test
    public void globalExceptionHandler(){
        Extension extension = ExtensionLoader.getExtensionLoader(Extension.class).getExtension(ExtensionImpl.class);
        extension.error("Hello");
    }
}
