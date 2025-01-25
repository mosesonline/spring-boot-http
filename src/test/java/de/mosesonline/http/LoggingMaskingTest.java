package de.mosesonline.http;

import ch.qos.logback.classic.Level;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonContent;
import org.springframework.test.json.JsonContentAssert;

import static de.mosesonline.http.WiremockUtils.createMapping;
import static de.mosesonline.http.WiremockUtils.deleteMapping;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class LoggingMaskingTest extends IntegrationTestBase {

    @Test
    void shouldHideSensibleDataFromRequest() throws Exception {
        final var backendId = "first";
        final var data = """
                        {
                            "data":"first",
                            "dateTime": "2024-12-22T19:31:17.123",
                            "status": "OK"
                         }""";
        final var stubId = createMapping("""
                {
                     "request": {
                         "method": "GET",
                         "url": "/test/%s"
                     },
                     "response": {
                         "status": 200,
                         "jsonBody": %s,
                         "headers": {
                             "Content-Type": "application/json",
                             "Cache-Control": "no-cache"
                         }
                     }
                 }""".formatted("first", data));
        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("x-backend", backendId)
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(200)
                    .header("X-Session-Id", notNullValue())
                    .body(notNullValue());
            assertThat(memoryAppender.getLoggedEvents())
                    .satisfiesOnlyOnce(event -> {
                        assertThat(event.getLevel()).isEqualTo(Level.TRACE);
                        assertThat(event.getLoggerName()).isEqualTo( org.zalando.logbook.Logbook.class.getName());
                        new JsonContentAssert(new JsonContent(event.getFormattedMessage()))
                                .hasPathSatisfying("$.body.status", value -> assertThat(value).isEqualTo("OK"))
                                .hasPathSatisfying("$.body.data", value -> assertThat(value).isEqualTo("replacement"))
                                .hasPathSatisfying("$.body.decimalData", value -> assertThat(value).isEqualTo(-1234));


                    });
        } finally {
            deleteMapping(stubId);
        }
    }
}
