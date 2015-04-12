package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.api.mvc.Call;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static play.mvc.Http.Context.Implicit.request;

@Entity
@JsonIgnoreProperties({"project"})
public class Screenshot extends Model {
    @Id
    public int id;

    @ManyToOne()
    @JoinColumn(name="project_id", referencedColumnName="id")
    @Constraints.Required
    public Project project;

    @Constraints.Required
    public String file;

    public int position = 0;

    public String getFile(){
        if(file == null){
            return null;
        }
        if(file.startsWith("http")){
            return file;
        }
        return new Call("GET", "").absoluteURL(request()) + file;
    }
}
