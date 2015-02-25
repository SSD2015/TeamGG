package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Vote {
    @Id
    public Integer id;

    @ManyToOne
    @JoinColumn(name="category_id", referencedColumnName="id")
    public VoteCategory category;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id")
    public User user;

    @ManyToOne
    @JoinColumn(name="project_id", referencedColumnName="id")
    public Project project;

    @Constraints.Required
    public int score = 1;

    public Date date;

    public static Model.Finder<Integer, Vote> find = new Model.Finder<Integer, Vote>(
            Integer.class, Vote.class
    );
}
