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
public class MessageManager {

    private static final String ERROR_FILE_PREFIX = "message_";

    private static final String ERROR_FILE_SUFFIX = ".properties";

    private static final Logger LOGGER = Logger.getLogger(MessageManager.class);

    private static final String DEFAULT_LANGUAGE = System.getProperty("user.language", "zh");

    private static final Map<String, MessageManager> I18N_UTILS = new ConcurrentHashMap<>();

    private final Map<String, String> messageMap = new ConcurrentHashMap<>();

    private MessageManager() {
    }

    private static String getResourcesName(String language) {
        return ERROR_FILE_PREFIX + language + ERROR_FILE_SUFFIX;
    }

    public static MessageManager getInstance() {
        return getInstance(DEFAULT_LANGUAGE);
    }

    public static String get(String code, Object... args) {
        return getInstance().getMessage(code, args);
    }

    public static MessageManager getInstance(String language) {
        language = language.intern();
        MessageManager messageManager = I18N_UTILS.get(language);
        if (messageManager == null) {
            synchronized (language) {
                messageManager = I18N_UTILS.get(language);
                if (messageManager == null) {
                    I18N_UTILS.putIfAbsent(language, new MessageManager());
                    messageManager = I18N_UTILS.get(language);
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
                                messageManager.messageMap.putIfAbsent(code, message);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
            }
        }
        return messageManager;
    }

    public String getMessage(String code, Object... args) {
        String value = messageMap.get(code);
        return String.format(value, args);
    }
}
