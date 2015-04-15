package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import helper.Assert;
import helper.WithApplicationDB;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static play.test.Helpers.cookie;

public class ApiAuthControllerTest extends WithApplicationDB {
    public static final String LOGIN = "/api/auth/login";
    public static final String CHECK = "/api/auth/check";

    @Test
    public void testLoginSuccess() throws Exception {
        ObjectNode req = Json.newObject();
        req.put("username", "dummy");
        req.put("password", "dummy");

        FakeRequest request = fakeRequest(POST, LOGIN).withJsonBody(req);
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertNotNull("id is not null", body.findPath("id").intValue());
        assertEquals("username is the given username", "dummy", body.findPath("username").textValue());
    }

    @Test
    public void testLoginFail() throws Exception {
        FakeRequest request = fakeRequest(POST, LOGIN)
                .withJsonBody(Json.parse("{\"username\": \"invalid-test\", \"password\": \"invalid-test\"}"));
        Result result = routeAndCall(request, 5);
        assertEquals(401, status(result));
        assertEquals("application/json", contentType(result));

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("id is null", body.findPath("id").isNull());
        assertTrue("username is null", body.findPath("username").isNull());
    }

    @Test
    public void testCheckSuccess() throws Exception {
        FakeRequest request = fakeRequest(GET, CHECK).withSession("user", "1");
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertEquals("id is 1", 1, body.findPath("id").intValue());
        assertEquals("username is dummy", "dummy", body.findPath("username").textValue());
    }

    @Test
    public void testCheckFail() throws Exception {
        FakeRequest request = fakeRequest(GET, CHECK);
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("id is null", body.findPath("id").isNull());
        assertTrue("body is null", body.findPath("username").isNull());
    }


}