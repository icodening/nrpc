package cn.icodening.rpc.config;

import cn.icodening.rpc.core.extension.ExtensionLoader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author icodening
 * @date 2021.03.16
 */
public class ConfigManager {

    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Map<String, ConfigurationRepository> CONFIGURATION_REPOSITORY_MAP = new ConcurrentHashMap<>(4);

    private ConfigManager() {
        List<ConfigurationRepository> configurationRepositories = ExtensionLoader.getExtensionLoader(ConfigurationRepository.class).getAllExtension();
        for (ConfigurationRepository configurationRepository : configurationRepositories) {
            CONFIGURATION_REPOSITORY_MAP.put(configurationRepository.type(), configurationRepository);
        }
    }

    public static String getSystemConfigString(String name) {
        return INSTANCE.CONFIGURATION_REPOSITORY_MAP.get("system").getString(name);
    }

    public static void setSystemConfigString(String name, String value) {
        INSTANCE.CONFIGURATION_REPOSITORY_MAP.get("system").addConfig(name, value);
    }

    public static String getString(String name, String key) {
        return INSTANCE.CONFIGURATION_REPOSITORY_MAP.get(name).getString(key);
    }

    public static boolean getBoolean(String name, String key) {
        Boolean ret = INSTANCE.CONFIGURATION_REPOSITORY_MAP.get(name).getBoolean(key);
        if (ret == null) {
            return false;
        }
        return ret;
    }

    public static Integer getInt(String name, String key) {
        return INSTANCE.CONFIGURATION_REPOSITORY_MAP.get(name).getInt(key);
    }
}
