package auth;

public interface Authenticator {

    public static final AuthenticatorUser INVALID = new InvalidAuthenticatorUser();

    /**
     * Attempts to authenticate user to a login service
     * @param username Username to try
     * @param password Password or other authentication token to try
     * @return Validated username. Must be unique on all services. If Authenticator.INVALID is returned the login has
     *         failed.
     */
    public AuthenticatorUser auth(String username, String password);

    interface AuthenticatorUser{
        public String getUsername();
        public String getName();
    }

}
