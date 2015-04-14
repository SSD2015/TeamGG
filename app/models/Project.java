package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.imgscalr.Scalr;
import play.api.mvc.Call;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import upload.Upload;
import upload.UploadFactory;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static play.mvc.Http.Context.Implicit.request;

@Entity
@JsonIgnoreProperties({"votes"})
public class Project extends Model {
    public static final int LOGO_SIZE = 160;

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
        // Ebean doesn't really understand @OrderBy, so we implement local sort
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

    public void setLogo(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        BufferedImage resized = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, LOGO_SIZE, LOGO_SIZE);
        image.flush();

        Upload upload = UploadFactory.get("logo", id.toString());
        upload.removeExisting();

        File temp = File.createTempFile("exceedvote", ".png");
        ImageIO.write(resized, "png", temp);
        resized.flush();
    }

}
