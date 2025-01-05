package de.mosesonline.http.api;

import de.mosesonline.http.model.UserData;

import java.util.UUID;

public interface SessionBackendPort {

    UserData requestUserData(UUID id);
}
