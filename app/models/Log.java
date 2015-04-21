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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static play.mvc.Http.Context.Implicit.request;

@Entity
public class Log extends Model {
    @Id
    public int id;

    @Constraints.Required
    public TYPE type;

    public static enum TYPE {
        LOGIN,
        PROJECT_EDIT
    }

    public Date time = new Date();

    public int ref = -1;
    public int ref2 = -1;
    public int ref3 = -1;

    @Lob
    public String metadata = "";

    public String toString(){
        if(type == TYPE.LOGIN){
            User user1 = User.find.byId(ref);

            if(ref2 != -1){
                User user2 = User.find.byId(ref2);
                return String.format("User login: switched %s to %s", user2.username, user1.username);
            }else {
                return String.format("User login: %s", user1.username);
            }
        }else if(type == TYPE.PROJECT_EDIT){
            User user = User.find.byId(ref);
            Project project = Project.find.byId(ref2);
            return String.format("Project edit: %s [%d] by %s", project.name, project.id, user.username);
        }
        return "Unsupported log entry";
    }

    public static Model.Finder<Integer, Log> find = new Model.Finder<Integer, Log>(
            Integer.class, Log.class
    );
}
