package helper;

import models.User;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.WithApplication;

public class WithApplicationInMemoryDB extends WithApplication {
    @Override
    protected FakeApplication provideFakeApplication() {
        return Helpers.fakeApplication(Helpers.inMemoryDatabase());
    }

    @Override
    public void startPlay() {
        super.startPlay();

        installFixation();
    }

    public void installFixation(){
        // these data are hard coded in many tests
        User voter = new User();
        voter.id = 1;
        voter.username = "dummy";
        voter.name = "Dummy voter";
        voter.type = User.TYPES.VOTER;
        voter.save();

        User instructor = new User();
        instructor.id = 2;
        instructor.username = "instructor";
        instructor.name = "Dummy instructor";
        instructor.type = User.TYPES.INSTRUCTOR;
        instructor.save();

        User org = new User();
        org.id = 3;
        org.username = "organizer";
        org.name = "Dummy organizer";
        org.type = User.TYPES.ORGANIZER;
        org.save();
    }
}
