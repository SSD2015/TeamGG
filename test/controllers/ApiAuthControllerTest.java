package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static play.test.Helpers.cookie;

public class ApiAuthControllerTest extends helper.WithApplicationInMemoryDB {

    @Test
    public void testLoginSuccess() throws Exception {
        requireKuTest();

        ObjectNode req = Json.newObject();
        req.put("username", play.Play.application().configuration().getString("test.kuuser"));
        req.put("password", play.Play.application().configuration().getString("test.kupassword"));

        Result result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiAuthController.login().toString()).withJsonBody(req)
                , 5);
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));

        JsonNode body = Json.parse(contentAsString(result));
        assertNotNull("id is not null", body.findPath("id").intValue());
        assertEquals("username is the given username", play.Play.application().configuration().getString("test.kuuser"), body.findPath("username").textValue());
    }

    @Test
    public void testLoginFail() throws Exception {
        Result result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiAuthController.login().toString())
                        .withJsonBody(Json.parse("{\"username\": \"invalid-test\", \"password\": \"invalid-test\"}"))
                , 5);
        assertEquals(403, status(result));
        assertEquals("application/json", contentType(result));

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("id is null", body.findPath("id").isNull());
        assertTrue("username is null", body.findPath("username").isNull());
    }

    @Test
    public void testCheckSuccess() throws Exception {
        Result result = routeAndCall(fakeRequest(GET, controllers.routes.ApiAuthController.check().toString()).withSession("user", "1"), 5);
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));

        JsonNode body = Json.parse(contentAsString(result));
        assertEquals("id is 1", 1, body.findPath("id").intValue());
        assertEquals("username is dummy", "dummy", body.findPath("username").textValue());
    }

    @Test
    public void testCheckFail() throws Exception {
        Result result = routeAndCall(fakeRequest(GET, controllers.routes.ApiAuthController.check().toString()), 5);
        assertEquals(200, status(result));
        assertEquals("application/json", contentType(result));

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("id is null", body.findPath("id").isNull());
        assertTrue("body is null", body.findPath("username").isNull());
    }

    public static void requireKuTest(){
        Assume.assumeNotNull(
                play.Play.application().configuration().getString("test.kuuser"),
                play.Play.application().configuration().getString("test.kupassword")
        );
    }
}