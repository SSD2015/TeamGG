package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class Project extends Model {

    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName="id")
    public Groups group;

    @Lob
    public String description;
    public String logo;

    public static Finder<Integer, Project> find = new Finder<Integer, Project>(
            Integer.class, Project.class
    );

}
