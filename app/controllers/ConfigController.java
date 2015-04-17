package controllers;

import models.VoteCategory;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;

import java.util.List;

public class ConfigController extends BaseController {

    public static Result show(){
        Form<VoteCategory> form = Form.form(VoteCategory.class);
        return ok(list(form));
    }

    public static Result saveCategory(){
        Form<VoteCategory> form = Form.form(VoteCategory.class);
        form = form.bindFromRequest();

        if(form.hasErrors()){
            return badRequest(list(form));
        }

        VoteCategory data = form.get();
        data.save();

        return redirect(controllers.routes.ConfigController.show());
    }

    private static Html list(Form<VoteCategory> form){
        List<VoteCategory> cat = VoteCategory.find.all();
        return views.html.config.render(cat, form);
    }
}
