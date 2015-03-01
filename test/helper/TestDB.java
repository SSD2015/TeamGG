package helper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.H2Platform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import models.*;
import play.api.test.FakeApplication;
import play.test.Helpers;

import java.util.Date;

public class TestDB {
    private EbeanServer server;
    private DdlGenerator ddl;

    /**
     * Call this after the application has been initialized
     */
    public TestDB(){
        setupDdl();
    }

    private void setupDdl(){
        server = Ebean.getServer("default");
        ddl = new DdlGenerator();
        ddl.setup((SpiEbeanServer) server, new H2Platform(), new ServerConfig());
    }

    private void createTable(){
        String createScript = ddl.generateCreateDdl();
        Ebean.execute(Ebean.createCallableSql(createScript));
    }

    /**
     * Remove all tables
     */
    public void dropTable(){
        String dropScript = ddl.generateDropDdl();
        Ebean.execute(Ebean.createCallableSql(dropScript));
    }

    /**
     * Recreate all tables and reload initial data
     * This is not automatically called when this class is initialized
     */
    public void resetDb(){
        dropTable();
        createTable();
        installFixation();
    }

    protected void installFixation(){
        // these data are hard coded in many tests
        // don't specify the id otherwise h2 id generation will break
        User voter = new User();
        voter.username = "dummy";
        voter.name = "Dummy voter";
        voter.type = User.TYPES.VOTER;
        voter.save();

        User instructor = new User();
        instructor.username = "instructor";
        instructor.name = "Dummy instructor";
        instructor.type = User.TYPES.INSTRUCTOR;
        instructor.save();

        User org = new User();
        org.username = "organizer";
        org.name = "Dummy organizer";
        org.type = User.TYPES.ORGANIZER;
        org.save();

        Groups group = new Groups();
        group.name = "Dummy group";
        group.number = 1;
        group.save();

        voter = new User();
        voter.username = "dummygroup";
        voter.name = "Dummy voter with group";
        voter.type = User.TYPES.VOTER;
        voter.group = group;
        voter.save();

        Project project = new Project();
        project.group = group;
        project.name = "Hello world";
        project.description = "Dummy project";
        project.save();

        Project project2 = new Project();
        project2.name = "Hello world 2";
        project2.description = "Dummy project 2";
        project2.save();

        VoteCategory bestVote = new VoteCategory();
        bestVote.name = "Best Dummy";
        bestVote.type = VoteCategory.VOTE_TYPE.BEST_OF;
        bestVote.save();

        VoteCategory scoreVote = new VoteCategory();
        scoreVote.name = "Score";
        scoreVote.type = VoteCategory.VOTE_TYPE.NUMERIC;
        scoreVote.save();

        Vote vote = new Vote();
        vote.category = bestVote;
        vote.project = project;
        vote.date = new Date();
        vote.user = voter;
        vote.save();
    }
}
