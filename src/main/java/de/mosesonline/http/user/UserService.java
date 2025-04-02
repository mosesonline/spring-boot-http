package de.mosesonline.http.user;

import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.UserData;
import de.mosesonline.http.model.UserSessionData;
import de.mosesonline.http.session.web.RequestSessionHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserService {
    private final SessionBackendPort sessionBackendService;
    private final RequestSessionHolder requestSessionHolder;

    UserService(SessionBackendPort sessionBackendService, RequestSessionHolder requestSessionHolder) {
        this.sessionBackendService = sessionBackendService;
        this.requestSessionHolder = requestSessionHolder;
    }

    public UserSessionData getUser(UUID userId) {
        UserData userData = sessionBackendService.requestUserData(userId);
        requestSessionHolder.getRequestSession().setUser(userData);
        return new UserSessionData(userData, requestSessionHolder.getRequestSession().getLastAccessedTime());
    }

    public UserSessionData getUserSession() {
        UserData userData = requestSessionHolder.getRequestSession().getUser();
        return new UserSessionData(userData, requestSessionHolder.getRequestSession().getLastAccessedTime());
    }
}
