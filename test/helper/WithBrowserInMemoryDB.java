package helper;

import com.typesafe.config.ConfigFactory;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithBrowser;

import java.io.File;
import java.util.Map;

public class WithBrowserInMemoryDB extends WithBrowser {
    private TestDB db;

    @Override
    protected FakeApplication provideFakeApplication() {
        Map<String, Object> root = ConfigFactory.parseFile(new File("conf/test.conf")).root().unwrapped();
        return Helpers.fakeApplication(root);
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
}
