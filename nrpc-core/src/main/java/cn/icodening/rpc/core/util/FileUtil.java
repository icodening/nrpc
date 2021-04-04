package cn.icodening.rpc.core.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author icodening
 * @date 2021.03.17
 */
public class FileUtil {
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            LOGGER.warn("file[" + file + "] is not exists", e);
        }
        return null;
    }
}
