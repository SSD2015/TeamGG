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
    public long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public VOTE_TYPE type;

    public enum VOTE_TYPE {
        BEST_OF, NUMERIC
    }

    public static Model.Finder<Long, VoteCategory> find = new Model.Finder<Long, VoteCategory>(
            Long.class, VoteCategory.class
    );

}
