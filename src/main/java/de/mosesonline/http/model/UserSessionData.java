package de.mosesonline.http.model;

import java.time.OffsetDateTime;

public record UserSessionData(UserData userData, OffsetDateTime lastAccessed) {
}
