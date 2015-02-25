package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Config {
    @Id
    public String k;

    @Lob
    public String value;


    private Map<String, String> _cache;

    public Map<String, String> getConfig(){
        if(_cache != null){
            return _cache;
        }
        Map<String, String> out = new HashMap<String, String>();

        for (Config config : find.all()) {
            out.put(config.k, config.value);
        }

        _cache = out;
        return out;
    }

    public static Model.Finder<String, Config> find = new Model.Finder<String, Config>(
            String.class, Config.class
    );
}
