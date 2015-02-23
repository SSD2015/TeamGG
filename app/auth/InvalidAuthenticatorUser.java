package auth;

/**
 * Special object to return when authenticator cannot validate user
 * Use Authenticator.INVALID to access an instance of this type
 */
public class InvalidAuthenticatorUser implements Authenticator.AuthenticatorUser {

    InvalidAuthenticatorUser(){
    }

    @Override
    public String getUsername() {
        return "Invalid";
    }

    @Override
    public String getName() {
        return "";
    }
}
