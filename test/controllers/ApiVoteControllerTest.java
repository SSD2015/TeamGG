package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import helper.Assert;
import helper.WithApplicationInMemoryDB;
import models.Vote;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ApiVoteControllerTest extends WithApplicationInMemoryDB {

    @Test
    public void testList() throws Exception {
        Result result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiVoteController.list().toString()),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        assertTrue("body must be array", body instanceof ArrayNode);

        ArrayNode arr = (ArrayNode) body;
        assertEquals("has two contents", 2, arr.size());

        JsonNode voteOne = arr.get(0);
        assertEquals("best vote id", 1, voteOne.findPath("id").intValue());
        assertNotNull("best vote name", voteOne.findPath("name").textValue());
        assertEquals("best vote type", "BEST_OF", voteOne.findPath("type").textValue());

        JsonNode voteTwo = arr.get(1);
        assertEquals("score vote id", 2, voteTwo.findPath("id").intValue());
        assertNotNull("score vote name", voteTwo.findPath("name").textValue());
        assertEquals("score vote type", "NUMERIC", voteTwo.findPath("type").textValue());
    }

    @Test
    public void testVoteError() throws Exception {
        Result result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(1, 1).toString()),
                5
        );
        assertEquals(401, status(result));
    }

    @Test
    public void testBestVote(){
        String userId = "1";

        Result result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(1, 1).toString())
                    .withSession("user", userId),
                5
        );
        Assert.assertJson(result);

        result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(1).toString())
                        .withSession("user", userId),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        JsonNode details = body.findPath("vote");
        assertTrue("have vote attribute", details.isObject());

        JsonNode firstVote = details.findPath("1");
        assertTrue("has vote in category 1", firstVote.isObject());
        assertEquals("has vote's category field", 1, firstVote.findPath("category").intValue());
        assertEquals("has vote's score field", 1, firstVote.findPath("score").intValue());
    }

    @Test
    public void testNumericVote(){
        // TODO
    }
}