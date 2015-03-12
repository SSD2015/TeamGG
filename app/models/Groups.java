package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"projects"})
public class Groups extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    public int number;

    @OneToMany(cascade=CascadeType.REMOVE, mappedBy = "group")
    public List<Project> projects;

    public static Finder<Integer, Groups> find = new Finder<Integer, Groups>(
            Integer.class, Groups.class
    );
}
