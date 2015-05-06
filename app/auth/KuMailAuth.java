package auth;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;

public class KuMailAuth implements Authenticator {

    private Session session;

    public KuMailAuth(){
        session = Session.getDefaultInstance(new Properties());
    }

    @Override
    public AuthenticatorUser auth(String username, String password) {
        try {
            Store connection = session.getStore(play.Play.application().configuration().getString("kuauth.protocol"));
            if (connection == null) {
                throw new RuntimeException("Configured mail protocol is not supported by javamail");
            }
            connection.connect(
                    play.Play.application().configuration().getString("kuauth.host"),
                    play.Play.application().configuration().getInt("kuauth.port", 993),
                    username,
                    password
            );
            connection.close();
        } catch (AuthenticationFailedException e){
            return Authenticator.INVALID;
        } catch (MessagingException e) {
            e.printStackTrace();
            return Authenticator.INVALID;
        }

        return new KuUser(username, username);
    }

    public static class KuUser implements Authenticator.AuthenticatorUser {

        private String username;
        private String name;


        public KuUser(String username, String name) {
            this.username = username;
            this.name = name;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
