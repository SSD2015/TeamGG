package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import helper.Assert;
import helper.WithApplicationInMemoryDB;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ApiProjectControllerTest extends WithApplicationInMemoryDB {
    @Test
    public void testListGuest(){
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.list().toString()),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("body must be array", body instanceof ArrayNode);

        ArrayNode arr = (ArrayNode) body;
        assertEquals("has one content", 1, arr.size());

        JsonNode project = arr.get(0);
        validateProject(project);
    }

    @Test
    public void testListUser(){
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.list().toString())
                    .withSession("user", "4"),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("body must be array", body instanceof ArrayNode);

        ArrayNode arr = (ArrayNode) body;
        assertEquals("has one content", 1, arr.size());

        JsonNode project = arr.get(0);
        validateProject(project);
        validateVote(project);
    }

    @Test
    public void testGetInfoGuest(){
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(1).toString()),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        validateProject(body);
    }

    @Test
    public void testGetInfoUser(){
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(1).toString())
                    .withSession("user", "4"),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        validateProject(body);
        validateVote(body);
    }

    @Test
    public void testGetInfoError(){
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(999999).toString()),
                5
        );
        assertEquals(404, status(result));
    }

    private void validateProject(JsonNode project){
        assertEquals("id", 1, project.findPath("id").intValue());
        assertNotNull("name", project.findPath("name").textValue());
        assertNotNull("description", project.findPath("description").textValue());
        JsonNode group = project.findPath("group");
        assertEquals("group id", 1, group.findPath("id").intValue());
        assertNotNull("group name", group.findPath("name").textValue());
        assertEquals("group number", 1, group.findPath("number").intValue());
    }

    private void validateVote(JsonNode project){
        JsonNode details = project.findPath("vote");
        assertTrue("have vote attribute", details.isObject());

        JsonNode firstVote = details.findPath("1");
        assertTrue("has vote in category 1", firstVote.isObject());
        assertEquals("has vote's category field", 1, firstVote.findPath("category").intValue());
        assertEquals("has vote's score field", 1, firstVote.findPath("score").intValue());
    }
}