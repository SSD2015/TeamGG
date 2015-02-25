package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Groups extends Model {
    @Id
    public Integer id;

    @Constraints.Required
    public String name;

    public int number;

    public static Finder<Integer, Groups> find = new Finder<Integer, Groups>(
            Integer.class, Groups.class
    );
}
