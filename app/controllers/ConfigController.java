package controllers;

import forms.ConfigForm;
import models.Config;
import models.VoteCategory;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Auth;

import java.util.List;
import java.util.Map;

public class ConfigController extends BaseController {

    @AddCSRFToken
    public static Result show() {
        if(!Auth.acl(Auth.ACL_TYPE.CONFIG)){
            return forbidden();
        }

        return ok(list());
    }

    @RequireCSRFCheck
    public static Result saveCategory() {
        if(!Auth.acl(Auth.ACL_TYPE.CONFIG)){
            return forbidden();
        }

        Form<VoteCategory> form = Form.form(VoteCategory.class);
        form = form.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(list(form, getConfigForm()));
        }

        VoteCategory data = form.get();
        data.save();

        return redirect(controllers.routes.ConfigController.show());
    }

    @RequireCSRFCheck
    public static Result saveConfig(){
        if(!Auth.acl(Auth.ACL_TYPE.CONFIG)){
            return forbidden();
        }

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

    @RequireCSRFCheck
    public static Result delete(){
        if(!Auth.acl(Auth.ACL_TYPE.CONFIG)){
            return forbidden();
        }

        DynamicForm form = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.valueOf(form.get("id"));
        }catch(NumberFormatException e){
            return badRequest();
        }

        VoteCategory cat = VoteCategory.find.byId(id);
        if(cat == null){
            return notFound("VoteCategory not found");
        }

        cat.delete();
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
