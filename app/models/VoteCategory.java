package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "vote_category")
@JsonIgnoreProperties({"votes"})
public class VoteCategory extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public VOTE_TYPE type;

    @OneToMany(cascade=CascadeType.REMOVE, mappedBy = "category")
    public List<Vote> votes;

    public enum VOTE_TYPE {
        BEST_OF, STAR
    }

    public static Model.Finder<Integer, VoteCategory> find = new Model.Finder<Integer, VoteCategory>(
            Integer.class, VoteCategory.class
    );

}
