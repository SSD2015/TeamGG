package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.imgscalr.Scalr;
import play.api.mvc.Call;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static play.mvc.Http.Context.Implicit.request;

@Entity
@JsonIgnoreProperties({"project"})
public class Screenshot extends Model {
    public static final int SCREENSHOT_SIZE_W = 1440;
    public static final int SCREENSHOT_SIZE_H = 900;

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
        if(file == null || file.isEmpty()){
            return null;
        }
        if(file.startsWith("http")){
            return file;
        }
        return new Call("GET", "").absoluteURL(request()) + file;
    }

    public static File resize(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        BufferedImage resized = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, SCREENSHOT_SIZE_W, SCREENSHOT_SIZE_H);
        image.flush();

        // Java encode PNG -> JPEG as JPEG with transparent, which does not exists
        // and probably be a reddish JPEG
        // http://stackoverflow.com/questions/464825/converting-transparent-gif-png-to-jpeg-using-java
        BufferedImage converted = new BufferedImage(resized.getWidth(null), resized.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = converted.createGraphics();
        g.drawImage(resized, 0, 0, converted.getWidth(), converted.getHeight(), Color.WHITE, null);
        resized.flush();

        File temp = File.createTempFile("exceedvote", ".jpg");
        ImageIO.write(converted, "jpg", temp);
        converted.flush();

        return temp;
    }
}
