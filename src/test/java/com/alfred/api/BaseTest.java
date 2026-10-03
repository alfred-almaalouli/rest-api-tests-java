package com.alfred.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Shared setup for all API tests: base URL, JSON content type
 * and request/response logging when a test fails.
 */
public abstract class BaseTest {

    protected static RequestSpecification spec;

    @BeforeAll
    static void setUp() {
        String baseUri = System.getProperty("baseUri", "https://jsonplaceholder.typicode.com");

        spec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
