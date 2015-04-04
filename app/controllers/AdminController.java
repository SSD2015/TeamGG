package controllers;

import models.User;
import play.filters.csrf.AddCSRFToken;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

public class AdminController extends Controller {
    @AddCSRFToken
    public static Result main(){
        if(!Auth.acl(Auth.ACL_TYPE.ADMIN)){
            if(Auth.isLoggedIn()){
                // user is not allowed to access the backoffice
                // redirect to the mobile client
                return redirect("/mobile/");
            }
            return redirect(controllers.routes.LoginController.login());
        }
        if(Auth.getUser().type == User.TYPES.VOTER){
            return redirect(controllers.routes.ProjectController.list());
        }
        return ok(views.html.admin.render());
    }
}
