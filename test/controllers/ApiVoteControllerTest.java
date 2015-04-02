package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import helper.Assert;
import helper.WithApplicationDB;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ApiVoteControllerTest extends WithApplicationDB {

    public static final String LIST = "/api/categories";
    public static final String VOTE = "/api/project/%d/vote/%d";

    /**
     * User to test vote with
     * Must has no existing votes
     */
    public static final String userId = "1";

    @Test
    public void testList() throws Exception {
        FakeRequest request = fakeRequest(GET, LIST);
        Result result = routeAndCall(request, 5);
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
        assertEquals("star vote id", 2, voteTwo.findPath("id").intValue());
        assertNotNull("star vote name", voteTwo.findPath("name").textValue());
        assertEquals("star vote type", "STAR", voteTwo.findPath("type").textValue());
    }

    @Test
    public void testVoteError() throws Exception {
        // unauthorized
        FakeRequest request = fakeRequest(POST, String.format(VOTE, 1, 1));
        Result result = routeAndCall(request, 5);
        assertEquals(401, status(result));

        // no category id
        request = fakeRequest(POST, String.format(VOTE, 1, 5555)).withSession("user", "1");
        result = routeAndCall(request, 5);
        assertEquals(404, status(result));

        // no project id
        request = fakeRequest(POST, String.format(VOTE, 5555, 1)).withSession("user", "1");
        result = routeAndCall(request, 5);
        assertEquals(404, status(result));

        // no both id
        request = fakeRequest(POST, String.format(VOTE, 5555, 5555)).withSession("user", "1");
        result = routeAndCall(request, 5);
        assertEquals(404, status(result));
    }

    @Test
    public void testBestVote(){
        // place vote
        FakeRequest request = fakeRequest(POST, String.format(VOTE, 1, 1)).withSession("user", userId);
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        // check
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 1)).withSession("user", userId);
        result = routeAndCall(request, 5);
        testHasVoteResult(result);

        // change vote
        request = fakeRequest(POST, String.format(VOTE, 2, 1)).withSession("user", userId);
        result = routeAndCall(request, 5);
        Assert.assertJson(result);

        // check
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 2)).withSession("user", userId);
        result = routeAndCall(request, 5);
        testHasVoteResult(result);

        // check that old project has no more vote
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 1)).withSession("user", userId);
        result = routeAndCall(request, 5);
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        JsonNode details = body.findPath("vote");
        assertTrue("have vote attribute", details.isObject());
        ObjectNode detailsNode = (ObjectNode) details;
        assertEquals("old project has no more vote", 0, detailsNode.size());
    }

    @Test
    public void testStarVote(){
        // place vote
        FakeRequest request = fakeRequest(POST, String.format(VOTE, 1, 2)).withSession("user", userId)
                .withJsonBody(Json.parse("{\"score\": 5}"));
        Result result = routeAndCall(request, 5);
        Assert.assertJson(result);

        // check
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 1)).withSession("user", userId);
        result = routeAndCall(request, 5);
        checkScoreIs(result, 5);

        // change vote
        request = fakeRequest(POST, String.format(VOTE, 1, 2)).withSession("user", userId)
                .withJsonBody(Json.parse("{\"score\": 3}"));
        result = routeAndCall(request, 5);
        Assert.assertJson(result);

        // check
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 1)).withSession("user", userId);
        result = routeAndCall(request, 5);
        checkScoreIs(result, 3);

        // vote for another project
        request = fakeRequest(POST, String.format(VOTE, 2, 2)).withSession("user", userId)
                .withJsonBody(Json.parse("{\"score\": 5}"));
        result = routeAndCall(request, 5);
        Assert.assertJson(result);

        // check
        request = fakeRequest(GET, String.format(ApiProjectControllerTest.INFO, 2)).withSession("user", userId);
        result = routeAndCall(request, 5);
        checkScoreIs(result, 5);
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

    private void checkScoreIs(Result result, int score){
        Assert.assertJson(result);

        JsonNode body = Json.parse(contentAsString(result));
        JsonNode details = body.findPath("vote");
        assertTrue("have vote attribute", details.isObject());

        JsonNode vote = details.findPath("2");
        assertTrue("has vote in category 2", vote.isObject());
        assertEquals("has vote's category field", 2, vote.findPath("category").intValue());
        assertEquals("has vote's score field", score, vote.findPath("score").intValue());
    }
}