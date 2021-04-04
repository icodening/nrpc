package cn.icodening.rpc.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author icodening
 * @date 2021.03.17
 */
public class FileUtil {
    private FileUtil() {
    }

    public static InputStream getInputStream(String fileName) {
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(fileName);
            if (resources == null || !resources.hasMoreElements()) {
                return null;
            }
            URL url = resources.nextElement();
            return url.openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
