package controllers;

import forms.AddUserForm;
import forms.LoginForm;
import models.User;
import play.data.Form;
import play.mvc.Result;
import utils.Pagination;

import java.util.List;

import static play.mvc.Http.Context.Implicit.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class UserController {
    private static final int ROW_PER_PAGE = 30;

    public static Result list(){
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);
        return list(addForm);
    }

    public static Result save(){
        Form<AddUserForm> addForm = Form.form(AddUserForm.class);

        addForm = addForm.bindFromRequest();
        if(addForm.hasErrors()){
            return list(addForm);
        }

        AddUserForm data = addForm.get();

        return list(addForm);
    }

    private static Result list(Form<AddUserForm> addForm){
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

        return ok(views.html.user_list.render(users, pager, addForm));
    }
}
