package controllers;

import models.User;
import play.filters.csrf.AddCSRFToken;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

public class AdminController extends BaseController {
    @AddCSRFToken
    public static Result main(){
        if(Auth.acl(Auth.ACL_TYPE.ADMIN)) {
            return ok(views.html.admin.render());
        }else if(Auth.acl(Auth.ACL_TYPE.PROJECT_EDIT)){
            return redirect(controllers.routes.ProjectController.list());
        }else if(Auth.isLoggedIn()){
            // user is not allowed to access the backoffice
            // redirect to the mobile client
            return redirect("/mobile/");
        }else{
            return redirect(controllers.routes.LoginController.login());
        }
    }
}
