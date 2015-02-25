package utils;

import auth.Authenticator;
import auth.KuMailAuth;
import com.avaje.ebean.Ebean;
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
            user = User.find.byId(Integer.parseInt(id));
        }catch(NumberFormatException e){
            user = null;
        }

        return user;
    }

    /**
     * Check whether the user can access a resource
     * @param aclType Type of resource
     * @return true if accessible
     */
    public static boolean acl(ACL_TYPE aclType){
        User user = getUser();

        if(user == null){
            return false;
        }

        switch(aclType){
            case ADMIN:
                return user.group != null || user.type.ordinal() >= User.TYPES.INSTRUCTOR.ordinal();
            case GROUP_SETTINGS:
                return user.group != null;
            case GROUPS: case USERS: case CONFIG:
                return user.type == User.TYPES.ORGANIZER;
            case VOTE_RESULT:
                return user.type == User.TYPES.ORGANIZER || user.type == User.TYPES.INSTRUCTOR;
            default:
                throw new IllegalArgumentException("Unknown ACL type");
        }
    }

    public enum ACL_TYPE{
        ADMIN, // can access admin page
        GROUP_SETTINGS, // can edit their own group
        GROUPS, // can edit any group
        USERS, // can add/view users
        CONFIG, // can config the application
        VOTE_RESULT // can see voting result
    }

    public static User login(String username, String password){
        Authenticator auth = new KuMailAuth();
        Authenticator.AuthenticatorUser result = auth.auth(username, password);

        if(result == Authenticator.INVALID){
            return null;
        }

        User user = User.find.where().eq("username", result.getUsername()).findUnique();
        if(user == null){
            // TODO: User shouldn't be created here. Remove this code when backoffice can create users in bulk
            user = new User();
            user.username = result.getUsername();
            user.type = User.TYPES.VOTER;
            Ebean.save(user);
        }

        session("user", String.valueOf(user.id));

        return user;
    }
}
