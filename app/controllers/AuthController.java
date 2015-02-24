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

public class AuthController extends Controller {
    public static Result login(){
        JsonNode body = request().body().asJson();
        String username = body.findPath("username").textValue();
        String password = body.findPath("password").textValue();

        Authenticator auth = new KuMailAuth();
        Authenticator.AuthenticatorUser result = auth.auth(username, password);

        User user = null;
        if(result != Authenticator.INVALID){
            user = User.find.where().eq("username", result.getUsername()).findUnique();
            if(user == null){
                // TODO: User shouldn't be created here. Remove this code when backoffice can create users in bulk
                user = new User();
                user.username = result.getUsername();
                user.type = User.TYPES.VOTER;
                Ebean.save(user);
            }

            session("user", String.valueOf(user.id));
        }

        return buildUser(user);
    }

    public static Result check(){
        String id = session("user");
        User user;
        try {
            user = User.find.byId(Long.parseLong(id));
        }catch(NumberFormatException e){
            user = null;
        }

        return buildUser(user);
    }

    private static Result buildUser(User user){
        ObjectNode out = Json.newObject();

        if(user == null){
            out.putNull("username");
        }else{
            out.put("id", user.id);
            out.put("username", user.username);
            out.put("name", user.getName());
            out.put("organization", user.organization);
            out.put("type", user.type.name());
            out.put("type_code", user.type.ordinal());
            out.putPOJO("group", user.group);
        }

        return ok(out);
    }
}
