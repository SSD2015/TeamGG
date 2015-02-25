package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vote_category")
public class VoteCategory {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public VOTE_TYPE type;

    public enum VOTE_TYPE {
        BEST_OF, NUMERIC
    }

    public static Model.Finder<Integer, VoteCategory> find = new Model.Finder<Integer, VoteCategory>(
            Integer.class, VoteCategory.class
    );

}
