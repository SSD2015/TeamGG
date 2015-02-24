package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Project extends Model {

    @Id
    public int id;

    @Constraints.Required
    public String name;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName="id")
    public Groups group;

    public String description;
    public String logo;

    public static Finder<Long, Project> find = new Finder<Long, Project>(
            Long.class, Project.class
    );

}
