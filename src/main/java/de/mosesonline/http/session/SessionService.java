package de.mosesonline.http.session;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SessionService {
    private final DynamoDBSessionRepository dynamoDBSessionRepository;

    SessionService(DynamoDBSessionRepository dynamoDBSessionRepository) {
        this.dynamoDBSessionRepository = dynamoDBSessionRepository;
    }

    public RawRequestSession obtainRequestSession(String header) {
        RawRequestSession rawRequestSession = header != null ? dynamoDBSessionRepository.get(header) : null;
        if (rawRequestSession == null) {
            rawRequestSession = new RawRequestSession();
            rawRequestSession.setId(UUID.randomUUID());
            rawRequestSession.setCreatedAt(OffsetDateTime.now());
        }
        rawRequestSession.setLastAccessedTime(OffsetDateTime.now());
        return rawRequestSession;
    }

    public void saveRequestSession(RawRequestSession requestSession) {
        dynamoDBSessionRepository.save(requestSession);
    }
}
