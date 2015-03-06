package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import forms.AddUserForm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Auth;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties({"password"})
public class User extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    @Column(unique = true)
    public String username;

    public String password;

    @Constraints.Required
    public TYPES type = TYPES.VOTER;

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

    public void setPassword(String password){
        this.password = Auth.getHasher().encode(password);
    }

    public boolean checkPassword(String password){
        return Auth.getHasher().matches(password, this.password);
    }

    public void fromForm(AddUserForm data) {
        username = data.username;
        setPassword(data.password);
        name = data.name;
        organization = data.organization;
        type = data.type;
    }

    public enum TYPES {
        VOTER, INSTRUCTOR, ORGANIZER
    }

    public static Finder<Integer, User> find = new Finder<Integer, User>(
            Integer.class, User.class
    );
}
