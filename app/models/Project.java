package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Vote> getVotes(User user){
        return Vote.find.where()
                .eq("project_id", id)
                .eq("user_id", user.id)
                .findList();
    }

    public Map<Integer, Vote> getVoteMap(User user){
        List<Vote> votes = getVotes(user);
        Map<Integer, Vote> out = new HashMap<>(votes.size());

        for (Vote vote : votes) {
            out.put(vote.category.id, vote);
        }


        return out;
    }

}
