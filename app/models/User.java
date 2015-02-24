package models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"password"})
public class User extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String username;

    public String password;
    public TYPES type;

    public String name = "";
    public String organization = "";

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName="id")
    public Groups group;

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
