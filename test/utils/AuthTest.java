package utils;

import controllers.ApiAuthControllerTest;
import helper.Mock;
import models.User;
import org.fest.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;

import java.util.Collection;

import static org.junit.Assert.*;
import static play.mvc.Controller.session;

public class AuthTest extends helper.WithApplicationInMemoryDB {

    @Before
    public void setUp(){
        Mock.mockRequest();
    }

    @After
    public void tearDown(){
        Http.Context.current.remove();
    }

    @Test
    public void testIsLoggedIn() throws Exception {
        assertFalse("User is not logged in", Auth.isLoggedIn());
        login();
        assertTrue("User is logged in", Auth.isLoggedIn());
    }

    @Test
    public void testGetUser() throws Exception {
        assertNull(Auth.getUser());
        login();

        User user = Auth.getUser();
        assertNotNull(user);
        assertEquals(1, user.id.intValue());
        assertEquals("dummy", user.username);
    }

    @Test
    public void testAcl() throws Exception {
        for(int i = 0; i <= 4; i++) {
            // try logging in as various roles
            if(i > 0){
                session("user", String.valueOf(i));
            }
            for (Auth.ACL_TYPE type : Auth.ACL_TYPE.values()) {
                Collection<Auth.ACL_TYPE> allowed = java.util.Collections.emptyList();
                if(i == 2){
                    allowed = Collections.list(
                            Auth.ACL_TYPE.ADMIN,
                            Auth.ACL_TYPE.VOTE_RESULT
                    );
                }else if(i == 3){
                    allowed = Collections.list(
                            Auth.ACL_TYPE.ADMIN,
                            Auth.ACL_TYPE.GROUP_SETTINGS,
                            Auth.ACL_TYPE.GROUPS,
                            Auth.ACL_TYPE.USERS,
                            Auth.ACL_TYPE.CONFIG,
                            Auth.ACL_TYPE.VOTE_RESULT
                    );
                }else if(i == 4){
                    allowed = Collections.list(
                            Auth.ACL_TYPE.ADMIN,
                            Auth.ACL_TYPE.GROUP_SETTINGS
                    );
                }
                assertEquals(
                        String.format("User %d can/cannot perform %s", i, type),
                        allowed.contains(type), Auth.acl(type)
                );
            }
        }
    }

    @Test
    public void testLogin() throws Exception {
        assertNull(Auth.login("invalid", "invalid"));

        User user = Auth.login("dummy", "dummy");
        assertNotNull(user);
        assertEquals("dummy", user.username);

        // TODO: Test KU account (need to create account in TestDB)
    }

    private void login(){
        session("user", "1");
    }
}