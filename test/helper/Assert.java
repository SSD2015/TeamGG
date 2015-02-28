package helper;

import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

public class Assert {
    public static void assertJson(Result result){
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));
    }
}
