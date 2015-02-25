package controllers;

import play.mvc.Result;
import utils.Auth;

import static play.mvc.Results.*;

public class AdminController {
    public static Result main(){
        if(!Auth.acl(Auth.ACL_TYPE.ADMIN)){
            if(Auth.isLoggedIn()){
                return unauthorized("User is not allowed to access the backoffice");
            }
            return redirect(controllers.routes.LoginController.login());
        }
        return ok();
    }
}
