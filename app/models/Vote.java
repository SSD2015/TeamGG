package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Vote {
    @Id
    public Long id;

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

    public static Model.Finder<Long, Vote> find = new Model.Finder<Long, Vote>(
            Long.class, Vote.class
    );
}
