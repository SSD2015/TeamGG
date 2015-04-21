package controllers;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.apache.commons.collections.map.MultiKeyMap;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.*;

public class ApiVoteController extends Controller {

    public static Result list() {
        List<VoteCategory> voteCategories = VoteCategory.find.all();
        return ok(Json.toJson(voteCategories));
    }

    public static Result vote(int projectId, int categoryId){
        if(!Auth.isLoggedIn()){
            return unauthorized("You're not logged in");
        }

        Map<String, String> config = Config.getConfig();
        if(!config.containsKey("voteOpen") || config.get("voteOpen").equals("0")){
            return forbidden("Voting is closed");
        }

        // check whether the project and category exists
        Project project = Project.find.byId(projectId);
        VoteCategory voteCat = VoteCategory.find.byId(categoryId);
        if (project == null || voteCat == null)
        {
            return notFound("Nothing in there asshole");
        }

        JsonNode body = null;
        int score = 0;
        try {
            body = request().body().asJson();
        } catch(NullPointerException e) {
        }
        if(body != null) {
            score = body.findPath("score").intValue();
        }

        // check whether the user has made a vote already
        User user = Auth.getUser();

        ExpressionList<Vote> q = Vote.find.where()
                .eq("user_id", user.id)
                .eq("category_id", categoryId);

        if(voteCat.type == VoteCategory.VOTE_TYPE.STAR){
            q = q.eq("project_id", projectId);
        }

        Vote vote = q.findUnique();
        // vote is null if never vote, a vote object otherwise
        switch (voteCat.type) {
            case BEST_OF:
                if (vote == null) {
                    //allow to vote
                    vote = new Vote();
                    vote.category = voteCat;
                    vote.user = user;
                    vote.project = project;
                    vote.date = new Date();
                    vote.save();
                } else {
                    vote.project = project;
                    vote.date = new Date();
                    vote.update();
                }
                break;
            case STAR:
                if (score < 1 || score > 5) {
                    return badRequest("FUCK YOU, INPUT 1-5");
                }
                if (vote == null)
                {
                    vote = new Vote();
                    vote.category = voteCat;
                    vote.user = user;
                    vote.score = score;
                    vote.project = project;
                    vote.date = new Date();
                    vote.save();
                }
                else
                {
                    vote.score = score;
                    vote.date = new Date();
                    vote.update();
                }
                break;
        }
        // return the result
        return ok(Json.toJson(vote));
    }

    public static Result result(){
        String showResult = Config.getConfig().get("showResult");
        if(!Auth.acl(Auth.ACL_TYPE.VOTE_RESULT) && (showResult == null || !showResult.equals("1"))){
            return forbidden();
        }

        MultiKeyMap summary = Vote.summarize();
        ObjectNode out = Json.newObject();
        List<VoteCategory> categories = VoteCategory.find.all();
        for(Project project : Project.find.all()){
            ObjectNode projectData = Json.newObject();
            for(VoteCategory cat : categories){
                Vote.VoteAggregate score = (Vote.VoteAggregate) summary.get(project.id, cat.id);
                if(score == null){
                    continue;
                }
                ObjectNode node = score.asJson();
                node.remove("project");
                projectData.put(String.valueOf(cat.id), node);
            }
            out.put(String.valueOf(project.id), projectData);
        }
        return ok(out);
    }
}