package de.mosesonline.http.session;

import de.mosesonline.http.model.UserData;
import org.springframework.aot.hint.annotation.Reflective;

import java.time.OffsetDateTime;
import java.util.UUID;

@Reflective
public class RawRequestSession {
    private UUID id;
    private UserData user;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastAccessedTime;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public OffsetDateTime getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(OffsetDateTime lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }
}
