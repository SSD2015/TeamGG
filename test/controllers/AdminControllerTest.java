package controllers;

import helper.WithBrowserDB;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdminControllerTest extends WithBrowserDB {
    @Test
    public void testLinks() throws Exception {
        login();

        browser.goTo("/");
        browser.$(".adminrow").get(0).click();
        assertEquals("first link must be config list", browser.url(), "/conf");

        browser.goTo("/");
        browser.$(".adminrow").get(2).click();
        assertEquals("third link must be project list", browser.url(), "/projects");

        browser.goTo("/");
        browser.$(".adminrow").get(3).click();
        assertEquals("forth link must be user", browser.url(), "/users");

        browser.goTo("/");
        browser.$(".adminrow").get(4).click();
        assertEquals("fifth link must be voting result", browser.url(), "/vote_result");

        browser.goTo("/");
        browser.$(".adminrow").get(5).click();
        assertEquals("sixth link must be log", browser.url(), "/log");
    }
}