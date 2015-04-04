package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"projects", "user"})
public class Groups extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    public int number;

    @OneToMany(cascade=CascadeType.DETACH, mappedBy = "group")
    public List<Project> projects;

    @OneToMany(cascade=CascadeType.DETACH, mappedBy = "group")
    public List<User> user;

    public static Model.Finder<Integer, Groups> find = new Model.Finder<Integer, Groups>(
            Integer.class, Groups.class
    );
}
