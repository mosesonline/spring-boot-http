package de.mosesonline.http;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import de.mosesonline.http.session.DynamoDbSessionData;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

import java.util.UUID;


@Import({TestcontainersConfiguration.class, IntegrationTestBase.InitTestDataConfiguration.class})
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

    @TestConfiguration
    static class InitTestDataConfiguration {

        @Autowired
        private DynamoDbEnhancedClient client;

        @PostConstruct
        void init() {
            StaticTableSchema<DynamoDbSessionData> build = TableSchema.builder(DynamoDbSessionData.class)
                    .newItemSupplier(DynamoDbSessionData::new)
                    .addAttribute(UUID.class, a -> a.name("id")
                            .getter(DynamoDbSessionData::getId)
                            .setter(DynamoDbSessionData::setId)
                            .tags(StaticAttributeTags.primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("sessionData")
                            .getter(DynamoDbSessionData::getSessionData)
                            .setter(DynamoDbSessionData::setSessionData))
                    .build();
            client.table("dynamo_db_session_data", build).createTable();
        }
    }
}
