package auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KuMailAuthTest {

    @Test
    public void testAuthInvalid() throws Exception {
        KuMailAuth auth = new KuMailAuth();
        assertEquals(Authenticator.INVALID, auth.auth("hello", "world"));
    }

    @Test
    public void testAuthValid() throws Exception {
        if(play.Play.application().configuration().getString("test.kuuser") == null){
            System.out.println("Skipping testAuthValid because no test.kuuser, test.kupassword is declared in the" +
                    " configuration");
            return;
        }

        KuMailAuth auth = new KuMailAuth();
        assertEquals(
                play.Play.application().configuration().getString("test.kuuser"),
                auth.auth(
                        play.Play.application().configuration().getString("test.kuuser"),
                        play.Play.application().configuration().getString("test.kupassword")
                ).getUsername()
        );
    }
}