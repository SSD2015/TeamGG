package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static play.test.Helpers.cookie;

public class AuthControllerTest {

    private Http.Cookie cookie;

    @Test
    public void testLoginSuccess() throws Exception {
        running(fakeApplication(inMemoryDatabase()), new Runnable(){
            @Override
            public void run() {
                if(play.Play.application().configuration().getString("test.kuuser") == null){
                    System.out.println("Skipping testLoginSuccess because no test.kuuser, test.kupassword is declared in the" +
                            " configuration");
                    return;
                }

                ObjectNode req = Json.newObject();
                req.put("username", play.Play.application().configuration().getString("test.kuuser"));
                req.put("password", play.Play.application().configuration().getString("test.kupassword"));

                Result result = routeAndCall(
                        fakeRequest(POST, "/api/auth/login").withJsonBody(req)
                        , 5);
                assertEquals(200, status(result));
                assertEquals("application/json", contentType(result));

                JsonNode body = Json.parse(contentAsString(result));
                assertNotNull(body.findPath("id").intValue());
                assertEquals(play.Play.application().configuration().getString("test.kuuser"), body.findPath("username").textValue());

                cookie = cookie("PLAY_SESSION", result);
            }
        });
    }

    @Test
    public void testLoginFail() throws Exception {
        running(fakeApplication(inMemoryDatabase()), new Runnable(){
            @Override
            public void run() {
                Result result = routeAndCall(
                        fakeRequest(POST, "/api/auth/login")
                                .withJsonBody(Json.parse("{\"username\": \"invalid-test\", \"password\": \"invalid-test\"}"))
                        , 5);
                assertEquals(403, status(result));
                assertEquals("application/json", contentType(result));

                JsonNode body = Json.parse(contentAsString(result));
                assertEquals(0, body.findPath("id").intValue());
                assertEquals(null, body.findPath("username").textValue());
            }
        });
    }

    @Test
    public void testCheckSuccess() throws Exception {
        try {
            testLoginSuccess();
        } catch (Exception e) {
            return;
        }

        running(fakeApplication(inMemoryDatabase()), new Runnable(){
            @Override
            public void run() {
                if(cookie == null){
                    System.out.println("Skipping testCheckSuccess because testLoginSuccess failed");
                    return;
                }

                Result result = routeAndCall(fakeRequest(GET, "/api/auth/check").withCookies(cookie), 5);
                assertEquals(200, status(result));
                assertEquals("application/json", contentType(result));

                JsonNode body = Json.parse(contentAsString(result));
                assertNotNull(body.findPath("id").intValue());
                assertEquals(play.Play.application().configuration().getString("test.kuuser"), body.findPath("username").textValue());
            }
        });
    }

    @Test
    public void testCheckFail() throws Exception {
        running(fakeApplication(inMemoryDatabase()), new Runnable(){
            @Override
            public void run() {
                Result result = routeAndCall(fakeRequest(GET, "/api/auth/check"), 5);
                assertEquals(200, status(result));
                assertEquals("application/json", contentType(result));

                JsonNode body = Json.parse(contentAsString(result));
                assertEquals(0, body.findPath("id").intValue());
                assertNull(body.findPath("username").textValue());
            }
        });
    }
}