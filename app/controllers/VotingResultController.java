package controllers;

import models.Project;
import models.Vote;
import models.VoteCategory;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Auth;

public class VotingResultController extends BaseController {
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
