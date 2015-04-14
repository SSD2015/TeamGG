package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import forms.AddProjectForm;
import models.Project;
import models.Screenshot;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import upload.Upload;
import utils.Auth;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProjectController extends Controller {
    public static final long MAX_UPLOAD_SIZE = 2 * 1024 * 1024;
    public static final int MAX_SCREENSHOT = 8;

    @AddCSRFToken
    public static Result list(){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT)){
            return forbidden();
        }

        Form<AddProjectForm> form = Form.form(AddProjectForm.class);
        return ok(renderList(form));
    }

    @RequireCSRFCheck
    public static Result save(){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT_ALL)){
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
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT_ALL)){
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

    @AddCSRFToken
    public static Result show(int id){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT)){
            return forbidden();
        }

        Project project = Project.find.byId(id);

        if(project == null){
            return notFound();
        }

        Result permError = checkEditAcl(project);
        if(permError != null){
            return permError;
        }

        Form<Project> form = Form.form(Project.class);
        form = form.fill(project);

        return ok(views.html.project_edit.render(project, form));
    }

    @RequireCSRFCheck
    public static Result update(int id){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT)){
            return forbidden();
        }

        Project project = Project.find.byId(id);

        if(project == null){
            return notFound();
        }

        Result permError = checkEditAcl(project);
        if(permError != null){
            return permError;
        }

        Form<Project> form = Form.form(Project.class);
        form = form.fill(project);
        form = form.bindFromRequest();
        DynamicForm dynamicForm = Form.form().bindFromRequest();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart logo = body.getFile("logo");

        if(logo != null){
            if(logo.getFile().length() > MAX_UPLOAD_SIZE){
                form.reject("Logo is larger than the maximum allowed");
            }
            if(!Upload.IMG_EXT.contains(logo.getFilename().substring(logo.getFilename().lastIndexOf(".")).toLowerCase())){
                form.reject("Logo is not in allowed format");
            }
        }

        if(form.data().get("group.name").trim().isEmpty()){
            form.reject("group.name", "This field is required.");
        }

        if(form.hasErrors()){
            return badRequest(views.html.project_edit.render(project, form));
        }

        // DO NOT SAVE THIS directly, it's a HUGE security hole
        Project data = form.get();

        // Copy each fields so we know exactly which fields are allowed
        if(project.group != null) {
            project.group.name = data.group.name;
            project.group.update();
        }
        project.name = data.name;
        project.description = data.description;

        if(dynamicForm.get("ssOrder") != null){
            String orders = dynamicForm.get("ssOrder");

            if(orders.isEmpty()){
                for(Screenshot item : project.screenshots){
                    item.delete();
                }
            }else{
                String[] order = orders.split(",");
                int length = Math.min(order.length, MAX_SCREENSHOT);
                Set<Integer> foundId = new HashSet<Integer>();

                // sort and discovery sent item
                for(int i = 0; i < length; i++){
                    int orderId = Integer.parseInt(order[i]);
                    for(Screenshot item : project.screenshots){
                        if(item.id == orderId){
                            item.position = i;
                            item.update();
                            foundId.add(orderId);
                            break;
                        }
                    }
                }

                // remove items not sent
                for(Screenshot item : project.screenshots){
                    if(!foundId.contains(item.id)){
                        item.delete();
                    }
                }
            }
        }

        // save uploaded files
        if(logo != null) {
            try {
                project.setLogo(logo.getFile());
            } catch (IOException e) {
                return internalServerError();
            }
        }

        project.update();

        return redirect(controllers.routes.ProjectController.show(id));
    }

    @RequireCSRFCheck // not really checked since request use XHR
    public static Result uploadScreenshot(int id){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT)){
            return forbidden();
        }

        Project project = Project.find.byId(id);

        if(project == null){
            return notFound();
        }

        if(project.screenshots.size() >= MAX_SCREENSHOT){
            return badRequest("Project has full screenshot quota");
        }

        Result permError = checkEditAcl(project);
        if(permError != null){
            return permError;
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");

        if(file == null){
            return badRequest();
        }

        if(file.getFile().length() > MAX_UPLOAD_SIZE){
            return badRequest("Screenshot is larger than the maximum allowed");
        }
        if(!Upload.IMG_EXT.contains(file.getFilename().substring(file.getFilename().lastIndexOf(".")).toLowerCase())){
            return badRequest("Screenshot is not in allowed format");
        }

        Screenshot ss;

        Ebean.beginTransaction();
        try {

            int weight = 0;

            for(Screenshot item : project.screenshots){
                weight = Math.max(weight, item.position);
            }

            ss = new Screenshot();
            ss.project = project;
            ss.position = weight + 1;
            ss.save();

            ss.setFile(file.getFile());
            ss.update();
            Ebean.commitTransaction();
        } catch (IOException e) {
            Ebean.endTransaction();
            return internalServerError("Upload process fail");
        } finally {
            Ebean.endTransaction();
        }

        return ok(Json.toJson(ss));
    }

    private static Html renderList(Form<AddProjectForm> form){
        Query<Project> query = Project.find.orderBy("group.number ASC, id ASC");

        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT_ALL)){
            query.where()
                    .eq("group_id", Auth.getUser().group.id);
        }

        return views.html.project_list.render(
                query.findList(),
                form
        );
    }

    private static Result checkEditAcl(Project project){
        if(!Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT_ALL)){
            // if user is not organizer, they must belong to group of the project
            if(project.group == null){
                return forbidden();
            }else if(Auth.getUser().group.id != project.group.id){
                return forbidden();
            }
        }
        return null;
    }
}
