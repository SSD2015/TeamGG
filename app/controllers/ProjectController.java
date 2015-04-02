package controllers;

import forms.AddProjectForm;
import models.Project;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Auth;

public class ProjectController extends Controller {
    @AddCSRFToken
    public static Result list(){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_LIST)){
            return forbidden();
        }

        Form<AddProjectForm> form = Form.form(AddProjectForm.class);
        return ok(renderList(form));
    }

    @RequireCSRFCheck
    public static Result save(){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_LIST)){
            return forbidden();
        }

        Form<AddProjectForm> form = Form.form(AddProjectForm.class);
        form = form.bindFromRequest();

        if(form.hasErrors()){
            return badRequest(renderList(form));
        }

        AddProjectForm data = form.get();
        Project project = data.asProject();
        project.save();

        return redirect(controllers.routes.ProjectController.list());
    }

    @RequireCSRFCheck
    public static Result delete(){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_LIST)){
            return forbidden();
        }

        DynamicForm form = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.valueOf(form.get("id"));
        }catch(NumberFormatException e){
            return badRequest();
        }

        Project project = Project.find.byId(id);
        if(project == null){
            return notFound();
        }

        project.delete();
        return redirect(controllers.routes.ProjectController.list());
    }

    private static Html renderList(Form<AddProjectForm> form){
        return views.html.project_list.render(
                Project.find.orderBy("group.number ASC, id ASC").findList(),
                form
        );
    }
}
