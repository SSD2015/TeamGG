package forms;

import models.Config;

import java.util.Map;

public class ConfigForm{
    public String announcement = "";
    public boolean voteOpen = false;
    public boolean allowMemberEdit = false;

    public static ConfigForm fromDatabase(){
        Map<String, String> config = Config.getConfig();
        ConfigForm form = new ConfigForm();

        form.announcement = config.get("announcement");
        if(config.containsKey("voteOpen")) {
            form.voteOpen = config.get("voteOpen").equals("1");
        }
        if(config.containsKey("allowMemberEdit")) {
            form.allowMemberEdit = config.get("allowMemberEdit").equals("1");
        }

        return form;
    }
}
