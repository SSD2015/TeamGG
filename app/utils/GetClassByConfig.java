package utils;

import com.typesafe.config.ConfigException;

public class GetClassByConfig {
    @SuppressWarnings("unchecked")
    public static <T> T get(String key){
        try {
            Class<? extends T> cls = getClass(key);
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigException.BadValue(key, "Class cannot be instantiated", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(String key){
        String clsName = play.Play.application().configuration().getString(key);
        try {
            return (Class<? extends T>) Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            throw new ConfigException.BadValue(key, "Class not found", e);
        } catch (ClassCastException e){
            throw new ConfigException.BadValue(key, "Class is not supported", e);
        }
    }
}
