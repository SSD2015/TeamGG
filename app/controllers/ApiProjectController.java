package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Project;
import models.User;
import models.Vote;
import play.libs.Json;
import play.mvc.Result;
import utils.Auth;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

public class ApiProjectController {

    public static Result list() {
        List<Project> projects = Project.find.all();

        List<JsonNode> out = new ArrayList<JsonNode>(projects.size());
        User user = Auth.getUser();

        for (Project project : projects)
        {
            ObjectNode node = (ObjectNode) Json.toJson(project);
            if(user != null) {
                List<Vote> voteList = project.getVotes(user);
                List<JsonNode> votes = new ArrayList<JsonNode>(voteList.size());

                for (Vote vote : voteList) {
                    ObjectNode voteObj = Json.newObject();
                    voteObj.put("category", vote.category.id);
                    voteObj.put("score", vote.score);
                    votes.add(voteObj);
                }


                node.put("vote", Json.toJson(votes));
            }
            out.add(node);
        }


        return ok(Json.toJson(out));
    }

    public static Result getInfo(int id) {
        Project project = Project.find.byId(id);
        if (project != null)
        {
            return ok(Json.toJson(project));
        }
        else
        {
            return notFound("ID NOT FOUND!!!");
        }
    }

}
