package controllers;

import forms.LoginForm;
import models.User;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

import static play.mvc.Http.Context.Implicit.session;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class LoginController extends BaseController {
    @AddCSRFToken
    public static Result login(){
        Form<LoginForm> loginForm = Form.form(LoginForm.class);
        return ok(views.html.login.render(loginForm));
    }

    @RequireCSRFCheck
    public static Result doLogin(){
        Form<LoginForm> loginForm = Form.form(LoginForm.class);

        loginForm = loginForm.bindFromRequest();
        if(loginForm.hasErrors()){
            return badRequest(views.html.login.render(loginForm));
        }

        LoginForm data = loginForm.get();

        User user = Auth.login(data.username, data.password);

        if(user != null){
            return redirect(controllers.routes.AdminController.main());
        }else{
            loginForm.reject("Authentication failed");
            return badRequest(views.html.login.render(loginForm));
        }
    }

    @RequireCSRFCheck
    public static Result logout() {
        session().remove("user");
        return redirect(controllers.routes.LoginController.login());
    }
}
