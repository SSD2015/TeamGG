package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import helper.Assert;
import helper.WithApplicationInMemoryDB;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ApiProjectControllerTest extends WithApplicationInMemoryDB {
    public static final String LIST = "/api/project";
    public static final String INFO = "/api/project/%d";

    /**
     * User to test get project vote with
     * Must have existing votes
     */
    public static final String userId = "4";

    @Test
    public void testListGuest(){
        FakeRequest request = fakeRequest(GET, LIST);
        Result result = routeAndCall(request, 5);

        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("body must be array", body instanceof ArrayNode);

        ArrayNode arr = (ArrayNode) body;
        assertEquals("has two content", 2, arr.size());

        JsonNode project = arr.get(0);
        validateProject(project);
    }

    @Test
    public void testListUser(){
        FakeRequest request = fakeRequest(GET, LIST).withSession("user", userId);
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("body must be array", body instanceof ArrayNode);

        ArrayNode arr = (ArrayNode) body;
        assertEquals("has two content", 2, arr.size());

        JsonNode project = arr.get(0);
        validateProject(project);
        validateVote(project);
    }

    @Test
    public void testGetInfoGuest(){
        FakeRequest request = fakeRequest(GET, String.format(INFO, 1));
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        validateProject(body);
    }

    @Test
    public void testGetInfoUser(){
        FakeRequest request = fakeRequest(GET, String.format(INFO, 1)).withSession("user", userId);
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        validateProject(body);
        validateVote(body);
    }

    @Test
    public void testGetInfoError(){
        FakeRequest request = fakeRequest(GET, String.format(INFO, 9999));
        Result result = routeAndCall(request, 5);
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