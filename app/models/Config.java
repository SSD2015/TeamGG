package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Config extends Model {
    @Id
    public String k;

    @Lob
    public String value;

    public static Map<String, String> getConfig(){
        Map<String, String> out = new HashMap<String, String>();

        for (Config config : find.all()) {
            out.put(config.k, config.value);
        }

        return out;
    }

    public static void saveConfig(Map<String, String> data){
        for(String key : data.keySet()){
            Config cfg = Config.find.byId(key);
            if(cfg == null){
                cfg = new Config();
                cfg.k = key;
                cfg.value = data.get(key);
                cfg.save();
            }else{
                cfg.k = key;
                cfg.value = data.get(key);
                cfg.update();
            }
        }
    }

    public static Model.Finder<String, Config> find = new Model.Finder<String, Config>(
            String.class, Config.class
    );
}
