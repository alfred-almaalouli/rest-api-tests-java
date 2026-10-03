# REST API Test Automation (Java)

![API Tests](https://github.com/alfred-almaalouli/rest-api-tests-java/actions/workflows/tests.yml/badge.svg)

Automated API tests for a public REST API ([JSONPlaceholder](https://jsonplaceholder.typicode.com)), written in **Java** with **REST Assured**, **JUnit 5** and **Maven**. The tests run automatically in **GitHub Actions** on every push.

## What is tested

| Area | Checks |
|------|--------|
| GET | list of all posts, single post, nested resources (comments of a post), users with nested address/company data |
| Query parameters | filtering posts by `userId` (data-driven, 3 users) |
| POST / PUT / PATCH / DELETE | create, replace, partially update and delete a post, correct status codes (201 / 200) |
| Negative tests | invalid ids (0, 101, 9999) return **404** |
| Contract | JSON schema validation of a post, unique ids, valid e-mail format |
| Non-functional | response time below 3 seconds, `Content-Type: application/json` |

## Tech stack

- Java 17
- REST Assured (incl. JSON schema validator)
- JUnit 5 (parameterized tests, display names)
- Jackson (Java objects ↔ JSON)
- Maven
- GitHub Actions (CI, test reports as artifact)

## Project structure

```
src/test/java/com/alfred/api/
  BaseTest.java       shared request setup (base URL, JSON, logging on failure)
  PostsTest.java      CRUD, filtering, negative and schema tests for /posts
  UsersTest.java      data checks, response time and headers for /users
  model/Post.java     data object for request and response bodies
src/test/resources/schemas/
  post-schema.json    expected structure of a post
```

## Run the tests

```bash
mvn test
```

Another environment can be tested without changing the code:

```bash
mvn test -DbaseUri=https://my-test-server.example
```

## Design decisions

- **Shared request specification** – base URL, headers and logging are defined once in `BaseTest`.
- **Readable tests** – the given / when / then style of REST Assured follows the structure of a test case (precondition, action, expected result).
- **Data-driven tests** – `@ParameterizedTest` covers several ids and users with one test method.
- **Logging only on failure** – request and response are printed only when a check fails, which keeps the CI log clean.
- **Configurable base URL** – the same tests can run against different environments.

## Author

Alfred Al Maalouli – [GitHub](https://github.com/alfred-almaalouli)
