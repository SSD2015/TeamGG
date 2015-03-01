package helper;

import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithBrowser;

public class WithBrowserInMemoryDB extends WithBrowser {
    private TestDB db;

    @Override
    protected FakeApplication provideFakeApplication() {
        return Helpers.fakeApplication(Helpers.inMemoryDatabase());
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
