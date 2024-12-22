package de.mosesonline.http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.stream.Stream;

import static de.mosesonline.http.WiremockUtils.*;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class HttpApplicationTest {

    @LocalServerPort
    private Integer port;

    private MemoryLoggingAppender memoryAppender;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setupLogging() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        memoryAppender = new MemoryLoggingAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    void stopLogging() {
        memoryAppender.stop();
    }

    @ParameterizedTest
    @MethodSource("getSimpleCallData")
    void simpleCall(String backendId, String data) throws Exception {
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
                 }""".formatted(backendId, data));
        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("x-backend", backendId)
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(200)
                    .body(Matchers.equalTo("{\"data\":\"%s\",\"dateTime\":\"2024-12-22T18:31:17.123Z\",\"decimalData\":0.0,\"status\":\"OK\"}".formatted(backendId)));
        } finally {
            deleteMapping(stubId);
        }
    }

    @Test
    void timeoutCall() throws Exception {
        final var stubId = createMapping("""
                {
                     "request": {
                         "method": "GET",
                         "url": "/test/first"
                     },
                     "response": {
                         "status": 200,
                         "fixedDelayMilliseconds": 5000,
                         "jsonBody": {
                            "data":"first",
                            "dateTime": "2024-12-22T19:31:17.123",
                            "status": "OK"
                         },
                         "headers": {
                             "Content-Type": "application/json",
                             "Cache-Control": "no-cache"
                         }
                     }
                 }""");
        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("x-backend", "first")
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(408)
                    .body(Matchers.containsString("timeout exception"));
            await().atMost(Duration.ofSeconds(5)).until(() -> memoryAppender.contains("request finished", Level.INFO));
        } finally {
            deleteMapping(stubId);
        }
    }

    @Test
    void shouldHandleDisabledBackend() {
        given()
                .contentType(ContentType.JSON)
                .header("x-backend", "third")
                .when()
                .get("/test")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Cannot find the backend with id: third"));

    }

    @Test
    void shouldRetryFourthBackend() throws Exception {
        final var stubId = createMapping("""
                {
                     "scenarioName": "Retry",
                     "requiredScenarioState": "Started",
                     "newScenarioState": "First call broke",
                     "request": {
                         "method": "GET",
                         "url": "/test/fourth"
                     },
                     "response": {
                         "status": 500,
                         "jsonBody": {
                            "message":"retry me plz"
                         },
                         "headers": {
                             "Content-Type": "application/json",
                             "Cache-Control": "no-cache"
                         }
                     }
                 }""");
        final var stubId2 = createMapping("""
                {
                     "scenarioName": "Retry",
                     "requiredScenarioState": "First call broke",
                     "newScenarioState": "Retry successful",
                     "request": {
                         "method": "GET",
                         "url": "/test/fourth"
                     },
                     "response": {
                         "status": 200,
                         "jsonBody": {
                            "data":"first",
                            "dateTime": "2024-12-22T19:31:17.123+0100",
                            "status": "OK"
                         },
                         "headers": {
                             "Content-Type": "application/json",
                             "Cache-Control": "no-cache"
                         }
                     }
                 }""");
        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("x-backend", "fourth")
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(200);
            assertThatScenarioHasState("Retry", "Retry successful");
        } finally {
            deleteMapping(stubId, stubId2);
        }
    }

    @Test
    void shouldTooManyRetriesFourthBackend() throws Exception {
        final var stubId = createMapping("""
                {
                     "request": {
                         "method": "GET",
                         "url": "/test/fourth"
                     },
                     "response": {
                         "status": 500,
                         "jsonBody": {
                            "message":"retry me plz"
                         },
                         "headers": {
                             "Content-Type": "application/json",
                             "Cache-Control": "no-cache"
                         }
                     }
                 }""");
        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("x-backend", "fourth")
                    .when()
                    .get("/test")
                    .then()
                    .statusCode(500)
                    .body(Matchers.containsString("retry me plz"));
        } finally {
            deleteMapping(stubId);
        }
    }

    @Test
    void shouldHandleUnknownBackend() {
        given()
                .contentType(ContentType.JSON)
                .header("x-backend", "X")
                .when()
                .get("/test")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Cannot find the backend with id: X"));

    }

    static Stream<Arguments> getSimpleCallData() {
        return Stream.of(
                Arguments.of("first", """
                        {
                            "data":"first",
                            "dateTime": "2024-12-22T19:31:17.123",
                            "status": "OK"
                         }"""),
                Arguments.of("second", """
                        {
                            "data":"second",
                            "dateTime": "2024-12-22T19:31:17.123+0100",
                            "status": "OK"
                         }""")
        );
    }

}
