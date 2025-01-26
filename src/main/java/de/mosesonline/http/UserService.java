package de.mosesonline.http;

import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.UserData;
import de.mosesonline.http.model.UserSessionData;
import de.mosesonline.http.session.web.RequestSessionHolder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.mosesonline.http.BackendRoutingConfiguration.BACKEND_QUALIFIER;

@Service
public class UserService {
    private final SessionBackendPort sessionBackendPort;
    private final RequestSessionHolder requestSessionHolder;

    UserService(@Qualifier(BACKEND_QUALIFIER) SessionBackendPort sessionBackendPort, RequestSessionHolder requestSessionHolder) {
        this.sessionBackendPort = sessionBackendPort;
        this.requestSessionHolder = requestSessionHolder;
    }

    public UserSessionData getUser(UUID userId) {
        UserData userData = sessionBackendPort.requestUserData(userId);
        requestSessionHolder.getRequestSession().setUser(userData);
        return new UserSessionData(userData, requestSessionHolder.getRequestSession().getLastAccessedTime());
    }

    public UserSessionData getUserSession() {
        UserData userData = requestSessionHolder.getRequestSession().getUser();
        return new UserSessionData(userData, requestSessionHolder.getRequestSession().getLastAccessedTime());
    }
}
