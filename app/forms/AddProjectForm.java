package forms;

import models.Groups;
import models.Project;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddProjectForm {
    @Constraints.Required
    public String name;
    public int group;

    public List<ValidationError> validate(){
        if(Groups.find.byId(group) == null){
            return Arrays.asList(new ValidationError("group", "No such group"));
        }

        return null;
    }

    public Project asProject(){
        Project project = new Project();
        project.group = Groups.find.byId(group);
        project.name = name;
        return project;
    }
}
