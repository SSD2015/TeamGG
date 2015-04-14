package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.api.mvc.Call;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

import static play.mvc.Http.Context.Implicit.request;

@Entity
@JsonIgnoreProperties({"votes"})
public class Project extends Model {

    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    @ManyToOne()
    @JoinColumn(name="group_id", referencedColumnName="id")
    public Groups group;

    @Lob
    public String description;
    public String logo;

    @OneToMany(cascade=CascadeType.REMOVE, mappedBy="project")
    public List<Vote> votes;

    @OneToMany(cascade=CascadeType.REMOVE, mappedBy="project")
    @OrderBy("position asc")
    public List<Screenshot> screenshots;

    public static Model.Finder<Integer, Project> find = new Model.Finder<Integer, Project>(
            Integer.class, Project.class
    );

    public List<Screenshot> getScreenshots(){
        Collections.sort(screenshots, new Comparator<Screenshot>() {
            @Override
            public int compare(Screenshot o1, Screenshot o2) {
                return o1.position - o2.position;
            }
        });
        return screenshots;
    }

    public String getLogo(){
        if(logo == null || logo.isEmpty()){
            return null;
        }
        if(logo.startsWith("http")){
            return logo;
        }
        return new Call("GET", "").absoluteURL(request()) + logo;
    }

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
