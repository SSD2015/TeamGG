package forms;

import models.User;
import play.data.validation.Constraints;

public class AddUserForm {
    @Constraints.Required
    public String username;
    public String password;
    public String name;
    public String organization;

    @Constraints.Required
    public User.TYPES type;
}
