package controllers;

import helper.WithBrowserDB;
import org.fluentlenium.adapter.util.SharedDriver;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;

import static org.junit.Assert.*;

@SharedDriver(type = SharedDriver.SharedType.PER_METHOD)
public class VotingResultControllerTest extends WithBrowserDB {

    @Test
    public void testResult() throws Exception {
        login();
        browser.goTo("/vote_result");

        FluentWebElement tr = browser.find("table tbody tr", 0);
        assertEquals("project 2 group number", "", tr.find("td", 0).getText());
        assertEquals("project 2 project name", "Hello world 2", tr.find("td", 1).getText());
        assertEquals("project 2 score in best vote", "1.0 (3 voters)", tr.find("td", 2).getText());
        assertEquals("project 2 score in star vote", "5.0 (2 voters)", tr.find("td", 3).getText());

        tr = browser.find("table tbody tr", 1);
        assertEquals("project 3 group number", "", tr.find("td", 0).getText());
        assertEquals("project 3 project name", "Hello world 3", tr.find("td", 1).getText());
        assertEquals("project 3 score in best vote", "No vote", tr.find("td", 2).getText());
        assertEquals("project 3 score in star vote", "No vote", tr.find("td", 3).getText());

        tr = browser.find("table tbody tr", 2);
        assertEquals("project 1 group number", "1", tr.find("td", 0).getText());
        assertEquals("project 1 project name", "Hello world", tr.find("td", 1).getText());
        assertEquals("project 1 score in best vote", "2.0 (3 voters)", tr.find("td", 2).getText());
        assertEquals("project 1 score in star vote", "3.5 (2 voters)", tr.find("td", 3).getText());
    }

    @Test
    public void testResultUnauthorized() throws Exception {
        browser.goTo("/vote_result");

        assertFalse(browser.pageSource().contains("Hello world"));
    }
}