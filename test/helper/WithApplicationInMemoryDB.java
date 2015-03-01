package helper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.H2Platform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import models.*;
import org.junit.AfterClass;
import org.junit.Before;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Date;

public class WithApplicationInMemoryDB extends WithApplication {
    private TestDB db;

    @Override
    protected FakeApplication provideFakeApplication() {
        return Helpers.fakeApplication(Helpers.inMemoryDatabase());
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
