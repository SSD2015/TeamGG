package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Groups extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String name;

    public int number;

    public static Finder<Long, Groups> find = new Finder<Long, Groups>(
            Long.class, Groups.class
    );
}
