package utils;

import models.User;

import static play.mvc.Controller.session;

public class Auth {
    public static boolean isLoggedIn(){
        return getUser() != null;
    }

    public static User getUser(){
        String id = session("user");
        User user;
        try {
            user = User.find.byId(Long.parseLong(id));
        }catch(NumberFormatException e){
            user = null;
        }

        return user;
    }
}
