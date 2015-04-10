package upload;

import utils.GetClassByConfig;

import java.lang.reflect.InvocationTargetException;

public class UploadFactory {
    public static Upload get(String key, String id){
        Class<? extends Upload> driver = GetClassByConfig.getClass("upload.driver");
        try {
            return driver.getConstructor(String.class, String.class)
                    .newInstance(key, id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Cannot instantiate uploader", e);
        }
    }
}
