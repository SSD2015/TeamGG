package auth;

import controllers.ApiAuthControllerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class KuMailAuthTest extends helper.WithApplicationInMemoryDB {

    @Test
    public void testAuthInvalid() throws Exception {
        KuMailAuth auth = new KuMailAuth();
        assertEquals(Authenticator.INVALID, auth.auth("hello", "world"));
    }

    @Test
    public void testAuthValid() throws Exception {
        ApiAuthControllerTest.requireKuTest();

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