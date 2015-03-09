package helper;

import com.typesafe.config.ConfigFactory;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.File;
import java.util.Map;

public class WithApplicationInMemoryDB extends WithApplication {
    private TestDB db;

    @Override
    protected FakeApplication provideFakeApplication() {
        Map<String, Object> root = ConfigFactory.parseFile(new File("conf/test.conf")).root().unwrapped();
        return Helpers.fakeApplication(root);
    }

    @Override
    public void startPlay() {
        super.startPlay();

        db = new TestDB();
        db.resetDb();
    }

    @Override
    public void stopPlay() {
        db.dropTable();

        super.stopPlay();
    }
}
