package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.AddUserForm;
import models.Screenshot;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Auth;
import utils.CsvUserLoader;
import utils.Pagination;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

public class UserController extends Controller {
    private static final int ROW_PER_PAGE = 30;

    @AddCSRFToken
    public static Result list(){
        if(!Auth.acl(Auth.ACL_TYPE.USERS)){
            return forbidden();
        }
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);
        return ok(list(addForm));
    }

    @RequireCSRFCheck
    public static Result save(){
        if(!Auth.acl(Auth.ACL_TYPE.USERS)){
            return forbidden();
        }

        Form<AddUserForm> addForm = Form.form(AddUserForm.class);

        addForm = addForm.bindFromRequest();

        if(addForm.data().containsKey("username")){
            String username = addForm.data().get("username");
            if(username == null){
                username = "";
            }
            if(User.find.where().eq("username", username).findUnique() != null){
                addForm.reject("username", "User with this username already exists");
            }
        }

        if(addForm.hasErrors()){
            return badRequest(list(addForm));
        }

        AddUserForm data = addForm.get();

        User user = new User();
        user.fromForm(data);
        user.save();

        return redirect(controllers.routes.UserController.list());
    }

    @RequireCSRFCheck
    public static Result edit(){
        if(!Auth.acl(Auth.ACL_TYPE.USERS)){
            return forbidden();
        }

        Map<String, String[]> body = request().body().asFormUrlEncoded();
        if(!body.containsKey("id")){
            return badRequest(errorJson("id is not given"));
        }

        int id;
        try{
            id = Integer.parseInt(body.get("id")[0]);
        }catch(NumberFormatException e){
            return badRequest(errorJson("id parse error"));
        }catch(ArrayIndexOutOfBoundsException e){
            return internalServerError(errorJson("id has zero length"));
        }

        User user = User.find.byId(id);

        if(user == null){
            return badRequest(errorJson("User not found"));
        }

        if(body.containsKey("delete")){
            if(id == Auth.getUser().id){
                return badRequest(errorJson("You may not delete yourself"));
            }
            user.delete();
            return noContent();
        }

        Form<AddUserForm> addForm = Form.form(AddUserForm.class);

        addForm = addForm.bindFromRequest();

        boolean changePassword = false;
        if(body.containsKey("changepw")){
            changePassword = body.get("changepw")[0].equals("on");
        }

        if(addForm.data().containsKey("username")){
            String username = addForm.data().get("username");
            if(username == null){
                username = "";
            }
            if(User.find.where().eq("username", username).ne("id", user.id).findUnique() != null){
                addForm.reject("username", "User with this username already exists");
            }
        }

        if(user.id == Auth.getUser().id){
            if(addForm.data().containsKey("type")){
                String type = addForm.data().get("type");
                if(!type.equals(user.type.toString())){
                    addForm.reject("type", "You may not demote yourself");
                }
            }
        }

        if(addForm.hasErrors()){
            return badRequest(addForm.errorsAsJson());
        }

        AddUserForm data = addForm.get();

        user.fromForm(data, changePassword);
        user.save();

        return ok(user.toAdminJson());
    }

    @RequireCSRFCheck
    public static Result upload(){
        if(!Auth.acl(Auth.ACL_TYPE.USERS)){
            return forbidden();
        }

        DynamicForm form = Form.form().bindFromRequest();
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);

        boolean skipExisting = form.get("skipexisting") != null;

        List<User> users = null;

        try{
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart file = body.getFile("file");
            CsvUserLoader loader = new CsvUserLoader(file.getFile());
            users = loader.read();
        }catch(IOException e){
            return internalServerError("Cannot read input file");
        }catch(CsvUserLoader.LoaderException e){
            form.reject(e.getMessage());
        }

        if(form.hasErrors()){
            return badRequest(list(addForm, form));
        }

        Ebean.beginTransaction();
        try {
            for (User user : users) {
                try {
                    user.save();
                } catch (PersistenceException e) {
                    if(e.getCause() instanceof SQLIntegrityConstraintViolationException && !skipExisting){
                        throw e;
                    }
                }
            }
            Ebean.commitTransaction();
        } catch(PersistenceException e){
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
                form.reject(e.getCause().getMessage());
            }else {
                form.reject(e.getMessage());
            }
        } finally {
            Ebean.endTransaction();
        }

        if(form.hasErrors()){
            return badRequest(list(addForm, form));
        }

        return redirect(controllers.routes.UserController.list());
    }

    private static Html list(Form<AddUserForm> addForm){
        return list(addForm, Form.form());
    }

    private static Html list(Form<AddUserForm> addForm, DynamicForm uploadForm){
        int page = 1;
        try {
            page = Integer.parseInt(request().getQueryString("page"));
        }catch(NumberFormatException e){
        }

        Pagination pager = new Pagination(ROW_PER_PAGE, User.find.findRowCount(), page);

        List<User> users = User.find.where()
                .setFirstRow(pager.getStartRow())
                .setMaxRows(ROW_PER_PAGE)
                .findList();

        return views.html.user_list.render(users, pager, addForm, uploadForm);
    }

    private static ObjectNode errorJson(String text){
        ObjectNode error = Json.newObject();
        error.put("error", text);
        return error;
    }
}
