package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        // unauthorized
        Result result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(1, 1).toString()),
                5
        );
        assertEquals(401, status(result));

        // no category id
        result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(1, 5555555).toString())
                    .withSession("user", "1"),
                5
        );
        assertEquals(404, status(result));

        // no project id
        result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(555555, 1).toString())
                        .withSession("user", "1"),
                5
        );
        assertEquals(404, status(result));

        // no both id
        result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(55555, 5555555).toString())
                        .withSession("user", "1"),
                5
        );
        assertEquals(404, status(result));
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
        testHasVoteResult(result);

        // change vote
        result = routeAndCall(
                fakeRequest(POST, controllers.routes.ApiVoteController.vote(2, 1).toString())
                        .withSession("user", userId),
                5
        );
        Assert.assertJson(result);

        result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(2).toString())
                        .withSession("user", userId),
                5
        );
        testHasVoteResult(result);

        // check that old project has no more vote
        result = routeAndCall(
                fakeRequest(GET, controllers.routes.ApiProjectController.getInfo(1).toString())
                        .withSession("user", userId),
                5
        );
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        JsonNode details = body.findPath("vote");
        assertTrue("have vote attribute", details.isObject());
        ObjectNode detailsNode = (ObjectNode) details;
        assertEquals("old project has no more vote", 0, detailsNode.size());
    }

    @Test
    public void testNumericVote(){
        // TODO
    }

    private void testHasVoteResult(Result result){
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        JsonNode details = body.findPath("vote");
        assertTrue("have vote attribute", details.isObject());

        JsonNode firstVote = details.findPath("1");
        assertTrue("has vote in category 1", firstVote.isObject());
        assertEquals("has vote's category field", 1, firstVote.findPath("category").intValue());
        assertEquals("has vote's score field", 1, firstVote.findPath("score").intValue());
    }
}