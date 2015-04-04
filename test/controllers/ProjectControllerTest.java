package controllers;

import helper.WithBrowserDB;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectControllerTest extends WithBrowserDB {

    @Test
    public void testList() {
        login();
        browser.goTo("/projects");
        assertEquals(3, browser.$("#datatable tbody tr").size());

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("name", "Hello world", row.find("td", 0).getText());
        assertEquals("group number", "1", row.find("td", 1).getText());
        assertEquals("group name", "Dummy group", row.find("td", 2).getText());

        row = browser.$("#datatable tbody tr").get(0);
        assertEquals("name", "Hello world 2", row.find("td", 0).getText());
        assertEquals("group number", "", row.find("td", 1).getText());
        assertEquals("group name", "", row.find("td", 2).getText());
    }

    @Test
    public void testSave() {
        String projectName = "Added project";
        String groupName = "Dummy group";

        login();
        browser.goTo("/projects");

        browser.fill("[action=\"/projects\"] [name=name]").with(projectName);
        browser.fillSelect("[action=\"/projects\"] [name=group]").withText(groupName);
        browser.submit("[action=\"/projects\"]");

        FluentWebElement row = browser.$("#datatable tbody tr").get(3);
        assertEquals("name", projectName, row.find("td", 0).getText());
        assertEquals("group number", "1", row.find("td", 1).getText());
        assertEquals("group name", groupName, row.find("td", 2).getText());
    }

    @Test
    public void testDelete() throws Exception {
        login();
        browser.goTo("/projects");

        assertEquals("original", 3, browser.$("#datatable tbody tr").size());

        // phantomjsdriver does not handle alert
        browser.executeScript("window.confirm = function(msg){return false;};");
        browser.findFirst(".removeform").submit();
        assertEquals("cancel remove", 3, browser.$("#datatable tbody tr").size());

        browser.executeScript("window.confirm = function(msg){return true;};");
        browser.findFirst(".removeform").submit();
        assertEquals("confirm remove", 2, browser.$("#datatable tbody tr").size());
    }

    @Test
    public void testResultUnauthorized() throws Exception {
        browser.goTo("/vote_result");

        assertFalse(browser.pageSource().contains("Hello world"));
    }
}