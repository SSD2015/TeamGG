package utils;

import auth.Authenticator;
import models.Config;
import models.Log;
import models.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

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
                return user.type.ordinal() >= User.TYPES.INSTRUCTOR.ordinal();
            case GROUPS: case USERS: case CONFIG:
                return user.type == User.TYPES.ORGANIZER;
            case VOTE_RESULT:
                return user.type == User.TYPES.ORGANIZER || user.type == User.TYPES.INSTRUCTOR;
            case PROJECT_EDIT:
                if(user.type != User.TYPES.ORGANIZER){
                    Map<String, String> config = Config.getConfig();
                    if(!config.containsKey("allowMemberEdit") || config.get("allowMemberEdit").equals("0")){
                        return false;
                    }
                }
                return user.type == User.TYPES.ORGANIZER || (
                        user.group != null && user.group.projects.size() > 0
                );
            case PROJECT_EDIT_ALL:
                return user.type == User.TYPES.ORGANIZER;
            case LOG:
                return user.type == User.TYPES.ORGANIZER || user.type == User.TYPES.INSTRUCTOR;
            default:
                throw new IllegalArgumentException("Unknown ACL type");
        }
    }

    public enum ACL_TYPE{
        ADMIN, // can access admin page
        GROUPS, // can edit any group
        USERS, // can add/view users
        CONFIG, // can config the application
        VOTE_RESULT, // can see voting result
        PROJECT_EDIT,
        PROJECT_EDIT_ALL,
        LOG // can view log
    }

    public static User login(String username, String password){
        User user = User.find.where().eq("username", username).findUnique();
        boolean success = false;

        if(user != null && user.hasPassword()){
            success = user.checkPassword(password);
        }

        if(!success && (user != null && !user.hasPassword())) {
            Authenticator auth = getAuth();
            Authenticator.AuthenticatorUser result = auth.auth(username, password);

            if(result == Authenticator.INVALID){
                return null;
            }
            if(user != null){
                success = result.getUsername().equals(user.username);
            }
        }

        if(success){
            Log log = new Log();
            log.type = Log.TYPE.LOGIN;
            log.ref = user.id;
            if(getUser() != null){
                log.ref2 = getUser().id;
            }
            log.save();

            session("user", String.valueOf(user.id));

            return user;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static PasswordEncoder getHasher(){
        return GetClassByConfig.get("auth.hasher");
    }

    @SuppressWarnings("unchecked")
    public static Authenticator getAuth(){
        return GetClassByConfig.get("auth.strategy");
    }
}
