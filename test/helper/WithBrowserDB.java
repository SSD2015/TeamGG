package helper;

import com.typesafe.config.ConfigFactory;
import models.User;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WithBrowserDB extends WithBrowser {
    private TestDB db;
    private Class<? extends WebDriver> driver = PhantomJSDriver.class;

    @Override
    protected FakeApplication provideFakeApplication() {
        Map<String, Object> root = ConfigFactory.parseFile(new File("conf/test.conf")).root().unwrapped();
        return Helpers.fakeApplication(root);
    }

    @Override
    protected TestBrowser provideBrowser(int port) {
        return Helpers.testBrowser(driver, port);
    }

    @Override
    public void startServer() {
        super.startServer();

        db = new TestDB();
        db.resetDb();
    }

    @Override
    public void stopServer() {
        db.dropTable();

        super.stopServer();
    }

    public void login(){
        login("organizer");
    }

    public void login(String username){
        browser.goTo("/login");
        browser.fill("input[name=username]").with(username);
        browser.fill("input[name=password]").with(username);
        browser.submit("form");

        User user = User.find.where().eq("username", username).findUnique();

        assertEquals("username must be shown in page", "Logged in as " + user.getName(), browser.$("#loggedas").getText());
    }
}
