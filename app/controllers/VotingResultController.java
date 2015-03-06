package controllers;

import models.Project;
import models.Vote;
import models.VoteCategory;
import play.mvc.Result;
import utils.Auth;

import static play.mvc.Results.forbidden;
import static play.mvc.Results.ok;

public class VotingResultController {
    public static Result result(){
        if(!Auth.acl(Auth.ACL_TYPE.VOTE_RESULT)){
            return forbidden();
        }
        return ok(views.html.vote_result.render(
                Project.find.orderBy("group.number").findList(),
                VoteCategory.find.all(),
                Vote.summarize()
        ));
    }
}
