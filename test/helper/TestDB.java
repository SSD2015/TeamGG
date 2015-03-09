package helper;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.H2Platform;
import com.avaje.ebean.config.dbplatform.MySqlPlatform;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import models.*;
import play.Logger;
import play.api.test.FakeApplication;
import play.test.Helpers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        ddl.setup((SpiEbeanServer) server, new MySqlPlatform(), new ServerConfig());
    }

    private void createTable(){
        String createScript = ddl.generateCreateDdl();
        executeSql(createScript);
    }

    /**
     * Remove all tables
     */
    public void dropTable(){
        String dropScript = ddl.generateDropDdl();
        executeSql(dropScript);
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

    private void executeSql(String sql) {
        Transaction tx = server.createTransaction();
        try {
            PreparedStatement stmt = tx.getConnection().prepareStatement(sql);
            stmt.execute();
            stmt.close();
        }catch(SQLException e){
            Logger.error("Cannot prepare SQL statement", e);
        }finally{
            tx.end();
        }
    }

    protected void installFixation(){
        // these data are hard coded in many tests
        // don't specify the id otherwise h2 id generation will break
        User voter = new User();
        voter.username = "dummy";
        voter.setPassword("dummy");
        voter.type = User.TYPES.VOTER;
        voter.save();

        User instructor = new User();
        instructor.username = "instructor";
        instructor.name = "Dummy instructor";
        instructor.type = User.TYPES.INSTRUCTOR;
        instructor.save();

        User org = new User();
        org.username = "organizer";
        org.setPassword("organizer");
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

        Project project3 = new Project();
        project3.name = "Hello world 3";
        project3.description = "Dummy project 3";
        project3.save();

        VoteCategory bestVote = new VoteCategory();
        bestVote.name = "Best Dummy";
        bestVote.type = VoteCategory.VOTE_TYPE.BEST_OF;
        bestVote.save();

        VoteCategory scoreVote = new VoteCategory();
        scoreVote.name = "Score";
        scoreVote.type = VoteCategory.VOTE_TYPE.STAR;
        scoreVote.save();

        // best vote: 2 for project 1

        Vote vote = new Vote();
        vote.category = bestVote;
        vote.project = project;
        vote.date = new Date();
        vote.user = voter;
        vote.save();

        vote = new Vote();
        vote.category = bestVote;
        vote.project = project;
        vote.date = new Date();
        vote.user = instructor;
        vote.save();

        vote = new Vote();
        vote.category = bestVote;
        vote.project = project2;
        vote.date = new Date();
        vote.user = org;
        vote.save();

        // star vote: 5.0 for project 2

        vote = new Vote();
        vote.category = scoreVote;
        vote.project = project2;
        vote.date = new Date();
        vote.user = voter;
        vote.score = 5;
        vote.save();

        vote = new Vote();
        vote.category = scoreVote;
        vote.project = project2;
        vote.date = new Date();
        vote.user = org;
        vote.score = 5;
        vote.save();

        // star vote: 3.5 for project 1

        vote = new Vote();
        vote.category = scoreVote;
        vote.project = project;
        vote.date = new Date();
        vote.user = voter;
        vote.score = 5;
        vote.save();

        vote = new Vote();
        vote.category = scoreVote;
        vote.project = project;
        vote.date = new Date();
        vote.user = instructor;
        vote.score = 2;
        vote.save();
    }
}
