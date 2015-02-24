package models;
import com.avaje.ebean.annotation.EnumMapping;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class User extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String username;

    public String password;
    public TYPES type;

    public String name;
    public String organization = "";

    @ManyToOne
    public Group group;

    public String getName(){
        if(!name.isEmpty()){
            return name;
        }else{
            return username;
        }
    }

    public enum TYPES {
        VOTER, INSTRUCTOR, ORGANIZER
    }

    public static Finder<Long, User> find = new Finder<Long, User>(
            Long.class, User.class
    );
}
