package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;

public class StaticServeController extends Controller {
    public static Result serve(String path, String name){
        return ok(new File(path, name));
    }
}
