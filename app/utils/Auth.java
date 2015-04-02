package utils;

import auth.Authenticator;
import auth.KuMailAuth;
import com.avaje.ebean.Ebean;
import models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public static boolean acl(ACL_TYPE type){
        User user = getUser();

        if(user == null){
            return false;
        }

        return acl(user, type);
    }

    /**
     * Check whether the user can access a resource
     * @param aclType Type of resource
     * @return true if accessible
     */
    public static boolean acl(User user, ACL_TYPE aclType){
        switch(aclType){
            case ADMIN:
                return user.group != null || user.type.ordinal() >= User.TYPES.INSTRUCTOR.ordinal();
            case GROUP_SETTINGS:
                return user.group != null || Auth.acl(ACL_TYPE.GROUPS);
            case GROUPS: case USERS: case CONFIG:
                return user.type == User.TYPES.ORGANIZER;
            case VOTE_RESULT:
                return user.type == User.TYPES.ORGANIZER || user.type == User.TYPES.INSTRUCTOR;
            case PROJECT_LIST:
                return user.type == User.TYPES.ORGANIZER;
            case PROJECT_EDIT:
                return user.type == User.TYPES.ORGANIZER || user.group != null;
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
        VOTE_RESULT, // can see voting result
        PROJECT_LIST,
        PROJECT_EDIT
    }

    public static User login(String username, String password){
        User user = User.find.where().eq("username", username).findUnique();
        boolean success = false;

        if(user != null && user.hasPassword()){
            success = user.checkPassword(password);
        }

        if(!success && (user != null && !user.hasPassword())) {
            Authenticator auth = new KuMailAuth();
            Authenticator.AuthenticatorUser result = auth.auth(username, password);

            if(result == Authenticator.INVALID){
                return null;
            }
            if(user != null){
                success = result.getUsername().equals(user.username);
            }
        }

        if(success){
            session("user", String.valueOf(user.id));
            return user;
        }
        return null;
    }

    public static PasswordEncoder getHasher(){
        return new BCryptPasswordEncoder();
    }
}
