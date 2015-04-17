package controllers;

import forms.ConfigForm;
import models.Config;
import models.VoteCategory;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;

import java.util.List;
import java.util.Map;

public class ConfigController extends BaseController {

    public static Result show() {
        return ok(list());
    }

    public static Result saveCategory() {
        Form<VoteCategory> form = Form.form(VoteCategory.class);
        form = form.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(list(form, getConfigForm()));
        }

        VoteCategory data = form.get();
        data.save();

        return redirect(controllers.routes.ConfigController.show());
    }

    public static Result saveConfig(){
        Form<ConfigForm> form = getConfigForm();
        form = form.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(list(Form.form(VoteCategory.class), form));
        }

        ConfigForm data = form.get();
        Map<String, String> config = Config.getConfig();

        config.put("announcement", data.announcement);
        config.put("allowMemberEdit", data.allowMemberEdit ? "1" : "0");
        config.put("voteOpen", data.voteOpen ? "1" : "0");

        Config.saveConfig(config);

        return redirect(controllers.routes.ConfigController.show());
    }

    private static Html list(){
        return list(Form.form(VoteCategory.class), getConfigForm());
    }

    private static Html list(Form<VoteCategory> catForm, Form<ConfigForm> configForm) {
        List<VoteCategory> cat = VoteCategory.find.all();
        return views.html.config.render(cat, catForm, configForm);
    }

    private static Form<ConfigForm> getConfigForm(){
        Form<ConfigForm> config = Form.form(ConfigForm.class);
        config = config.fill(ConfigForm.fromDatabase());
        return config;
    }
}
