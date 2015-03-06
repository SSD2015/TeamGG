package controllers;

import forms.AddUserForm;
import models.User;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Pagination;

import java.util.List;

import static play.mvc.Http.Context.Implicit.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class UserController {
    private static final int ROW_PER_PAGE = 30;

    public static Result list(){
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);
        return ok(list(addForm));
    }

    public static Result save(){
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);

        addForm = addForm.bindFromRequest();

        if(addForm.data().containsKey("username")){
            if(User.find.where().eq("username", addForm.data().getOrDefault("username", "")).findUnique() != null){
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

    private static Html list(Form<AddUserForm> addForm){
        int start = 0;
        try {
            start = Integer.parseInt(request().getQueryString("start"));
        }catch(NumberFormatException e){
        }

        Pagination pager = new Pagination(ROW_PER_PAGE, start);
        pager.setItemCount(User.find.findRowCount());

        List<User> users = User.find.where()
                .setFirstRow(start * ROW_PER_PAGE)
                .setMaxRows(ROW_PER_PAGE)
                .findList();

        return views.html.user_list.render(users, pager, addForm);
    }
}
