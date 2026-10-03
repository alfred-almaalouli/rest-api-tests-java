package com.alfred.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Users API")
class UsersTest extends BaseTest {

    private static final String EMAIL_PATTERN = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    @Test
    @DisplayName("GET /users returns 10 users with valid e-mail addresses")
    void allUsersHaveValidEmails() {
        given().spec(spec)
        .when().get("/users")
        .then()
                .statusCode(200)
                .body("size()", equalTo(10))
                .body("email", everyItem(matchesPattern(EMAIL_PATTERN)));
    }

    @Test
    @DisplayName("GET /users/1 contains address and company data")
    void userHasNestedData() {
        given().spec(spec)
        .when().get("/users/1")
        .then()
                .statusCode(200)
                .body("address.city", not(emptyOrNullString()))
                .body("address.geo.lat", notNullValue())
                .body("company.name", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET /users responds within 3 seconds")
    void responseTime() {
        given().spec(spec)
        .when().get("/users")
        .then()
                .statusCode(200)
                .time(lessThan(3L), TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Response has JSON content type")
    void contentTypeIsJson() {
        given().spec(spec)
        .when().get("/users/1")
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }
}
