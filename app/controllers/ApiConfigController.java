package controllers;

import models.Config;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class ApiConfigController extends Controller {
    public static Result getConfig(){
        return ok(Json.toJson(Config.getConfig()));
    }
}
