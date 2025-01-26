package de.mosesonline.http;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;


@Import({TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class IntegrationTestBase {
    @LocalServerPort
    private Integer port;
    protected MemoryLoggingAppender memoryAppender;

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

}
