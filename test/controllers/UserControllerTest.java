package controllers;

import helper.WithBrowserDB;
import org.fluentlenium.adapter.util.SharedDriver;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SharedDriver(type = SharedDriver.SharedType.ONCE)
public class UserControllerTest extends WithBrowserDB {

    @Test
    public void testList() throws Exception {
        login();
        browser.goTo("/users");

        assertEquals(4, browser.$("#datatable tbody tr").size());

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        assertEquals("name", "Dummy organizer", row.find("td", 1).getText());
        assertEquals("organization", "Testing ltd.", row.find("td", 2).getText());
        assertEquals("type", "ORGANIZER", row.find("td", 3).getText());
        assertEquals("group", "", row.find("td", 4).getText());
        assertTrue("has password", row.find("td", 5).findFirst("i").getAttribute("class").contains("ok"));

        row = browser.$("#datatable tbody tr").get(3);
        assertEquals("username", "dummygroup", row.find("td", 0).getText());
        assertEquals("name", "Dummy voter with group", row.find("td", 1).getText());
        assertEquals("type", "VOTER", row.find("td", 3).getText());
        assertEquals("group", "Dummy group (1)", row.find("td", 4).getText());
        assertTrue("has no password", row.find("td", 5).findFirst("i").getAttribute("class").contains("remove"));
    }

    @Test
    public void testSave() throws Exception {
        login();
        browser.goTo("/users");

        String username = "createdByTest";
        String password = "testpassword";
        String name = "This user is created by unit testing";

        browser.fill("[action=\"/users\"] [name=username]").with(username);
        browser.fill("[action=\"/users\"] [name=password]").with(password);
        browser.fill("[action=\"/users\"] [name=name]").with(name);
        browser.fillSelect("[action=\"/users\"] [name=type]").withValue("ORGANIZER");
        browser.submit("[action=\"/users\"]");

        FluentWebElement row = browser.$("#datatable tbody tr").get(4);
        assertEquals("username", username, row.find("td", 0).getText());
        assertEquals("name", name, row.find("td", 1).getText());
        assertEquals("type", "ORGANIZER", row.find("td", 3).getText());

        // logout
        logout();

        login(username, password);
        assertTrue("can be logged in", browser.pageSource().contains(name));
    }

    @Test
    public void testSaveDuplicateUsername() throws Exception {
        login();
        browser.goTo("/users");

        browser.fill("[action=\"/users\"] [name=username]").with("organizer");
        browser.submit("[action=\"/users\"]");

        assertEquals(4, browser.$("#datatable tbody tr").size());
    }

    @Test
    public void testEdit() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        String username = "edited";
        String name = "Edited with unit test";

        browser.fill("#editpanel [name=username]").with(username);
        browser.fill("#editpanel [name=name]").with(name);
        browser.submit("#editpanel");

        browser.await().untilPage().isLoaded();
        logout();
        login("edited", "organizer");
    }

    @Test
    public void testEditDuplicateUsername() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        browser.fill("#editpanel [name=username]").with("dummy");
        browser.submit("#editpanel");

        browser.await().untilPage().isLoaded();
        browser.goTo("/users");
        row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
    }

    @Test
    public void testEditPassword() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        String username = "edited";
        String name = "Edited with unit test";
        String password = "edited123";

        browser.fill("#editpanel [name=username]").with(username);
        browser.fill("#editpanel [name=name]").with(name);
        browser.$("#editpanel [name=changepw]").click();
        browser.fill("#editpanel [name=password]").with(password);
        browser.submit("#editpanel");

        browser.await().untilPage().isLoaded();
        logout();
        login(username, password);
    }

    @Test
    public void testEditRole() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(0);
        assertEquals("username", "dummy", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        browser.fillSelect("#editpanel [name=type]").withText("ORGANIZER");
        browser.submit("#editpanel");

        browser.await().untilPage().isLoaded();
        logout();
        login("dummy", "dummy");
    }

    @Test
    public void testEditDemoteSelf() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        browser.fillSelect("#editpanel [name=type]").withText("VOTER");
        browser.submit("#editpanel");

        browser.await().untilPage().isLoaded();

        browser.goTo("/");
        browser.goTo("/users");

        row = browser.$("#datatable tbody tr").get(2);
        assertEquals("type", "ORGANIZER", row.find("td", 3).getText());
    }



    @Test
    public void testDelete() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(3);
        assertEquals("username", "dummygroup", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        // phantomjsdriver does not handle alert
        browser.executeScript("window.confirm = function(msg){return false;};");
        browser.$("#deleteuser").click();

        browser.await().atMost(1, TimeUnit.SECONDS).untilPage().isLoaded();

        assertEquals("alert dismiss must cancel", 4, browser.$("#datatable tbody tr").size());

        browser.executeScript("window.confirm = function(msg){return true;};");
        browser.$("#deleteuser").click();

        browser.await().untilPage().isLoaded();
        browser.goTo("/users");
        assertEquals("user must be deleted", 3, browser.$("#datatable tbody tr").size());
        assertFalse("dummygroup must be the missing user", browser.$("#datatable tbody").getText().contains("dummygroup"));
    }

    @Test
    public void testDeleteSelf() throws Exception {
        login();
        browser.goTo("/users");

        FluentWebElement row = browser.$("#datatable tbody tr").get(2);
        assertEquals("username", "organizer", row.find("td", 0).getText());
        row.find(".editbtn").click();
        browser.await().atMost(1, TimeUnit.SECONDS).until("#editpanel").areDisplayed();

        browser.executeScript("window.confirm = function(msg){return true;}; window.alert = function(){};");
        browser.$("#deleteuser").click();

        browser.await().untilPage().isLoaded();
        // phantomjs seems to skip rendering of empty page
        browser.goTo("/");
        browser.goTo("/users");
        assertEquals(4, browser.$("#datatable tbody tr").size());
    }

    @Test
    public void testGuest() throws Exception {
        browser.goTo("/users");
        assertTrue(browser.$("#datatable").isEmpty());
    }

    @Test
    public void testInstructor() throws Exception {
        login("instructor", "instructor");
        browser.goTo("/users");
        assertTrue(browser.$("#datatable").isEmpty());
    }
}