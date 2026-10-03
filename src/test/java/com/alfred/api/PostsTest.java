package com.alfred.api;

import com.alfred.api.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Posts API")
class PostsTest extends BaseTest {

    @Test
    @DisplayName("GET /posts returns all 100 posts")
    void getAllPosts() {
        given().spec(spec)
        .when().get("/posts")
        .then()
                .statusCode(200)
                .body("size()", equalTo(100))
                .body("id", everyItem(notNullValue()));
    }

    @Test
    @DisplayName("GET /posts/1 returns the expected post")
    void getSinglePost() {
        given().spec(spec)
        .when().get("/posts/1")
        .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET /posts/1 matches the JSON schema")
    void postMatchesSchema() {
        given().spec(spec)
        .when().get("/posts/1")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @ParameterizedTest(name = "post id {0} does not exist -> 404")
    @ValueSource(ints = {0, 101, 9999})
    @DisplayName("GET with an invalid id returns 404")
    void getPostWithInvalidId(int id) {
        given().spec(spec)
        .when().get("/posts/" + id)
        .then()
                .statusCode(404);
    }

    @ParameterizedTest(name = "userId = {0}")
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("Filter posts by userId returns only that user's posts")
    void filterPostsByUser(int userId) {
        given().spec(spec)
                .queryParam("userId", userId)
        .when().get("/posts")
        .then()
                .statusCode(200)
                .body("size()", equalTo(10))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test
    @DisplayName("GET /posts/1/comments returns the comments of the post")
    void getCommentsOfPost() {
        given().spec(spec)
        .when().get("/posts/1/comments")
        .then()
                .statusCode(200)
                .body("size()", equalTo(5))
                .body("postId", everyItem(equalTo(1)))
                .body("email", everyItem(containsString("@")));
    }

    @Test
    @DisplayName("POST /posts creates a post and returns it with a new id")
    void createPost() {
        Post newPost = new Post(1, "Test automation", "Created with REST Assured");

        Post created = given().spec(spec)
                .body(newPost)
        .when().post("/posts")
        .then()
                .statusCode(201)
                .extract().as(Post.class);

        assertNotNull(created.getId());
        assertEquals(newPost.getTitle(), created.getTitle());
        assertEquals(newPost.getBody(), created.getBody());
        assertEquals(newPost.getUserId(), created.getUserId());
    }

    @Test
    @DisplayName("PUT /posts/1 replaces the post")
    void updatePost() {
        Post update = new Post(1, "Updated title", "Updated body");
        update.setId(1);

        given().spec(spec)
                .body(update)
        .when().put("/posts/1")
        .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Updated title"))
                .body("body", equalTo("Updated body"));
    }

    @Test
    @DisplayName("PATCH /posts/1 changes only the title")
    void patchPost() {
        given().spec(spec)
                .body("{\"title\": \"Only the title changed\"}")
        .when().patch("/posts/1")
        .then()
                .statusCode(200)
                .body("title", equalTo("Only the title changed"))
                .body("body", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("DELETE /posts/1 returns 200")
    void deletePost() {
        given().spec(spec)
        .when().delete("/posts/1")
        .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Post ids are unique")
    void postIdsAreUnique() {
        List<Integer> ids = given().spec(spec)
        .when().get("/posts")
        .then()
                .statusCode(200)
                .extract().jsonPath().getList("id", Integer.class);

        assertEquals(ids.size(), ids.stream().distinct().count());
    }
}
