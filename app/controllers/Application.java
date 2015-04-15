package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends BaseController {

    public static Result cors(String all){
        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response().setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        response().setHeader("Access-Control-Allow-Credentials", "true");
        return ok();
    }

}
