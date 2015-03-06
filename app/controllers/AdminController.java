package controllers;

import play.mvc.Result;
import utils.Auth;

import static play.mvc.Results.*;

public class AdminController {
    public static Result main(){
        if(!Auth.acl(Auth.ACL_TYPE.ADMIN)){
            if(Auth.isLoggedIn()){
                // user is not allowed to access the backoffice
                // redirect to the mobile client
                return redirect("/mobile/");
            }
            return redirect(controllers.routes.LoginController.login());
        }
        return ok(views.html.admin.render());
    }
}
