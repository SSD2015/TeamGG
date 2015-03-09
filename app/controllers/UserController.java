package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import forms.AddUserForm;
import models.User;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Auth;
import utils.Pagination;

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

        return ok(list(addForm));
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

        if(addForm.hasErrors()){
            return badRequest(addForm.errorsAsJson());
        }

        AddUserForm data = addForm.get();

        user.fromForm(data, changePassword);
        user.save();

        return ok(user.toAdminJson());
    }

    private static Html list(Form<AddUserForm> addForm){
        int start = 0;
        try {
            start = Integer.parseInt(request().getQueryString("start"));
        }catch(NumberFormatException e){
        }

        Pagination pager = new Pagination(ROW_PER_PAGE, User.find.findRowCount(), start);

        List<User> users = User.find.where()
                .setFirstRow(start * ROW_PER_PAGE)
                .setMaxRows(ROW_PER_PAGE)
                .findList();

        return views.html.user_list.render(users, pager, addForm);
    }

    private static ObjectNode errorJson(String text){
        ObjectNode error = Json.newObject();
        error.put("error", text);
        return error;
    }
}
