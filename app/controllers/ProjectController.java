package controllers;

import models.Project;
import play.libs.Json;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.*;

public class ProjectController {

    public static Result list() {
        List<Project> projects = Project.find.all();
        return ok(Json.toJson(projects));
    }

    public static Result getInfo(String id) {
        Project project;

        try
        {
            project = Project.find.byId(Integer.parseInt(id));
        }
        catch(NumberFormatException e)
        {
            return badRequest("FUCK YOU, ENTER INTEGER!!!");
        }
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
