package auth;

import helper.WithApplicationDB;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class KuMailAuthTest extends WithApplicationDB {

    @Test
    public void testAuthInvalid() throws Exception {
        KuMailAuth auth = new KuMailAuth();
        assertEquals(Authenticator.INVALID, auth.auth("hello", "world"));
    }

    @Test
    public void testAuthValid() throws Exception {
        requireKuTest();

        KuMailAuth auth = new KuMailAuth();
        assertEquals(
                play.Play.application().configuration().getString("test.kuuser"),
                auth.auth(
                        play.Play.application().configuration().getString("test.kuuser"),
                        play.Play.application().configuration().getString("test.kupassword")
                ).getUsername()
        );
    }

    public static void requireKuTest(){
        Assume.assumeNotNull(
                play.Play.application().configuration().getString("test.kuuser"),
                play.Play.application().configuration().getString("test.kupassword")
        );
    }
}