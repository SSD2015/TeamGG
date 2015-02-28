package controllers;

import models.Project;
import models.User;
import models.Vote;
import models.VoteCategory;
import play.libs.Json;
import play.mvc.Result;
import utils.Auth;

import java.util.Date;
import java.util.List;

import static play.mvc.Results.*;

public class ApiVoteController {

    public static Result list() {
        List<VoteCategory> voteCategories = VoteCategory.find.all();
        return ok(Json.toJson(voteCategories));
    }

    public static Result vote(int projectId, int categoryId){
        if(!Auth.isLoggedIn()){
            return unauthorized("You're not logged in");
        }

        // check whether the project and category exists
        Project project = Project.find.byId(projectId);
        VoteCategory voteCat = VoteCategory.find.byId(categoryId);
        if (project == null || voteCat == null)
        {
            return notFound("Nothing in there asshole");
        }

        // check whether the user has made a vote already
        User user = Auth.getUser();
        Vote vote = Vote.find.where()
                .eq("user_id", user.id)
                .eq("category_id", categoryId)
                .findUnique();
        // vote is null if never vote, a vote object otherwise
        if (voteCat.type == VoteCategory.VOTE_TYPE.BEST_OF) {
            if (vote == null)
            {
                //allow to vote
                vote = new Vote();
                vote.category = voteCat;
                vote.user = user;
                vote.project = project;
                vote.date = new Date();
                vote.save();
            }
            else
            {
                vote.project = project;
                vote.date = new Date();
                vote.update();
            }
        }
        // return the result
        return ok(Json.toJson(vote));
    }
}