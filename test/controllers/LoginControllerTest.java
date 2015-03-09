package controllers;

import helper.WithBrowserDB;
import org.fluentlenium.adapter.util.SharedDriver;
import org.junit.Test;

import static org.junit.Assert.*;

@SharedDriver(type = SharedDriver.SharedType.PER_METHOD)
public class LoginControllerTest extends WithBrowserDB {

    @Test
    public void testLoginFail() throws Exception {
        browser.goTo("/login");
        browser.fill("input[name=username]").with("invalid");
        browser.fill("input[name=password]").with("invalid");
        browser.submit("form");

        assertEquals("failed login must return to login page", "/login", browser.url());
        assertFalse("must not be logged in", browser.pageSource().contains("Logged in as"));
    }

    @Test
    public void testLogin() throws Exception {
        login();

        assertEquals("/", browser.url());
        assertTrue("username must be shown in page", browser.pageSource().contains("organizer"));
    }
}