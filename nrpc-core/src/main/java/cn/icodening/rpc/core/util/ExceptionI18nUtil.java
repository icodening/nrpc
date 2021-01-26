package cn.icodening.rpc.core.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.01.25
 */
public class ExceptionI18nUtil {

    private static final String ERROR_FILE_PREFIX = "message_";

    private static final String ERROR_FILE_SUFFIX = ".properties";

    private static final Logger LOGGER = Logger.getLogger(ExceptionI18nUtil.class);

    private static final String DEFAULT_LANGUAGE = System.getProperty("user.language", "zh");

    private static final Map<String, ExceptionI18nUtil> I18N_UTILS = new ConcurrentHashMap<>();

    private final Map<String, String> messageMap = new ConcurrentHashMap<>();

    private ExceptionI18nUtil() {
    }

    private static String getResourcesName(String language) {
        return ERROR_FILE_PREFIX + language + ERROR_FILE_SUFFIX;
    }

    public static ExceptionI18nUtil getInstance() {
        return getInstance(DEFAULT_LANGUAGE);
    }

    public static String get(String code, Object... args) {
        return getInstance().getMessage(code, args);
    }

    public static ExceptionI18nUtil getInstance(String language) {
        ExceptionI18nUtil exceptionI18nUtil = I18N_UTILS.get(language);
        if (exceptionI18nUtil == null) {
            synchronized (language) {
                exceptionI18nUtil = I18N_UTILS.get(language);
                if (exceptionI18nUtil == null) {
                    I18N_UTILS.putIfAbsent(language, new ExceptionI18nUtil());
                    exceptionI18nUtil = I18N_UTILS.get(language);
                    try {
                        Enumeration<URL> resources =
                                Thread.currentThread().getContextClassLoader()
                                        .getResources(getResourcesName(language));
                        while (resources.hasMoreElements()) {
                            URL url = resources.nextElement();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                            String ret;
                            while ((ret = bufferedReader.readLine()) != null) {
                                int index = ret.indexOf("=");
                                String code = ret.substring(0, index);
                                String message = ret.substring(index + 1);
                                exceptionI18nUtil.messageMap.putIfAbsent(code, message);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
            }
        }
        return exceptionI18nUtil;
    }

    public String getMessage(String code, Object... args) {
        String value = messageMap.get(code);
        return String.format(value, args);
    }
}
