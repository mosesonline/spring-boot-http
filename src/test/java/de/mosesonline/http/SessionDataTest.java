package de.mosesonline.http;

import de.mosesonline.http.model.UserSessionData;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class SessionDataTest extends IntegrationTestBase {

    private static final String VALID_DATE_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(?:\\.\\d+)?([+\\-][0-9]{2}:[0-9]{2}|Z)";

    @Test
    void userHasSessionDataLastAccessedValue() {
        final var userId = UUID.randomUUID();
        given()
                .contentType(ContentType.JSON)
                .header("x-backend", "first")
                .when()
                .get("/test/users/" + userId)
                .then()
                .statusCode(200)
                .header("X-Session-Id", notNullValue())
                .body("userData.id", equalTo(userId.toString()))
                .body("userData.username", startsWith("first "))
                .body("lastAccessed", matchesPattern(VALID_DATE_PATTERN));
    }

    @Test
    void fetchedUserShouldBeInSession() {
        final var userId = UUID.randomUUID();
        ExtractableResponse<Response> response1 = given()
                .contentType(ContentType.JSON)
                .header("x-backend", "first")
                .when()
                .get("/test/users/" + userId)
                .then()
                .statusCode(200)
                .header("X-Session-Id", notNullValue())
                .extract();

        UserSessionData firstResponse = response1.body().as(UserSessionData.class);

        given()
                .contentType(ContentType.JSON)
                .header("x-backend", "first")
                .header("X-Session-Id", response1.header("X-Session-Id"))
                .when()
                .get("/test/users-session")
                .then()
                .statusCode(200)
                .header("X-Session-Id", notNullValue())
                .body("userData.id", equalTo(firstResponse.userData().id().toString()))
                .body("userData.username", equalTo(firstResponse.userData().username()))
                .body("lastAccessed", notNullValue())
                .body("lastAccessed", not(equalTo(firstResponse.lastAccessed().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))))
                .extract();
    }

    @Test
    void userEmptyUserSessionDataForSessionWithoutUser() {
        given()
                .contentType(ContentType.JSON)
                .header("x-backend", "first")
                .header("X-Session-Id", UUID.randomUUID().toString())
                .when()
                .get("/test/users-session")
                .then()
                .statusCode(200)
                .header("X-Session-Id", notNullValue())
                .body("userData", nullValue())
                .body("lastAccessed", notNullValue())
                .body("lastAccessed", matchesPattern(VALID_DATE_PATTERN));
    }
}
