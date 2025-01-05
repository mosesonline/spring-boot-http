package de.mosesonline.http.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Component
class DynamoDBSessionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBSessionRepository.class);
    private final DynamoDbTemplate dynamoDbTemplate;
    private final ObjectMapper objectMapper;

    DynamoDBSessionRepository(@Lazy DynamoDbTemplate dynamoDbTemplate, ObjectMapper objectMapper) {
        this.dynamoDbTemplate = dynamoDbTemplate;
        this.objectMapper = objectMapper;
    }

    RawRequestSession get(String sessionId) {
        try {
            DynamoDbSessionData load = dynamoDbTemplate.load(Key.builder().partitionValue(sessionId).build(), DynamoDbSessionData.class);
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

    void save(RawRequestSession requestSession) {
        try {
            dynamoDbTemplate.save(DynamoDbSessionData.from(requestSession.getId(), objectMapper.writeValueAsString(requestSession)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
