package cn.icodening.rpc.config;

import java.util.Map;

/**
 * @author icodening
 * @date 2021.03.15
 */
public interface ConfigurationRepository {

    String type();

    String getString(String name);

    default Byte getByte(String name) {
        String string = getString(name);
        return Byte.parseByte(string);
    }

    default Short getShort(String name) {
        String string = getString(name);
        return Short.parseShort(string);
    }

    default Integer getInt(String name) {
        String string = getString(name);
        return Integer.parseInt(string);
    }

    default Double getDouble(String name) {
        String string = getString(name);
        return Double.parseDouble(string);
    }

    default Float getFloat(String name){
        String string = getString(name);
        return Float.parseFloat(string);
    }

   default Boolean getBoolean(String name){
       String string = getString(name);
       return Boolean.parseBoolean(string);
   }

    Map<String, Object> getAll();

    void addConfig(String name, Object data);
}
