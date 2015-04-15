package controllers;

import auth.Authenticator;
import auth.KuMailAuth;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

public class ApiAuthController extends Controller {
    public static Result login(){
        JsonNode body = request().body().asJson();
        String username = body.findPath("username").textValue();
        String password = body.findPath("password").textValue();

        User user = Auth.login(username, password);

        if(user == null) {
            return unauthorized(buildUser(user));
        }

        return ok(buildUser(user));
    }

    public static Result check(){
        return ok(buildUser(Auth.getUser()));
    }

    public static Result logout(){
        ObjectNode out = Json.newObject();
        if(!Auth.isLoggedIn()){
            out.put("success", false);
            return ok(out);
        }

        session().remove("user");
        out.put("success", true);

        return ok(out);
    }

    private static ObjectNode buildUser(User user){
        ObjectNode out = Json.newObject();

        if(user == null){
            out.putNull("username");
            out.putNull("id");
        }else{
            out = (ObjectNode) Json.toJson(user);
            out.put("type_code", user.type.ordinal());
        }

        return out;
    }
}
