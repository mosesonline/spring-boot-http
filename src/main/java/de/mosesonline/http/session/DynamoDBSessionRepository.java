package de.mosesonline.http.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import static de.mosesonline.http.session.SessionConfiguration.TABLE_SCHEMA;

@Component
class DynamoDBSessionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBSessionRepository.class);
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final ObjectMapper objectMapper;

    DynamoDBSessionRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, ObjectMapper objectMapper) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.objectMapper = objectMapper;
    }

    @Reflective
    RawRequestSession get(String sessionId) {
        try {
            DynamoDbTable<DynamoDbSessionData> mappedTable = dynamoDbEnhancedClient.table("dynamo_db_session_data", TABLE_SCHEMA);
            DynamoDbSessionData load = mappedTable.getItem(Key.builder().partitionValue(sessionId).build());
            if (load == null) {
                return null;
            }
            return objectMapper.readValue(load.getSessionData(), RawRequestSession.class);
        } catch (ResourceNotFoundException e) {
            if (sessionId != null) {
                LOGGER.warn("Session {} not found", sessionId);
            }

            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Reflective
    void save(RawRequestSession requestSession) {
        DynamoDbTable<DynamoDbSessionData> mappedTable = dynamoDbEnhancedClient.table("dynamo_db_session_data", TABLE_SCHEMA);
        mappedTable.putItem(r -> {
            try {
                r.item(DynamoDbSessionData.from(requestSession.getId(), objectMapper.writeValueAsString(requestSession)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
