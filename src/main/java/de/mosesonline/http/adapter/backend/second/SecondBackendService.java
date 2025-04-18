package de.mosesonline.http.adapter.backend.second;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.QualifiedBackendService;
import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.UserData;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@QualifiedBackendService(backendId = "second")
class SecondBackendService implements BackendPort, SessionBackendPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondBackendService.class);
    private final SecondBackendClient backendClient;

    SecondBackendService(SecondBackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @TimeLimiter(name = "second-backend-service")
    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return backendClient.callSecond(testCase);
            } finally {
                LOGGER.info("request finished");
            }
        });
    }


    @Override
    public UserData requestUserData(UUID id) {
        return new UserData(id, "second " + UUID.randomUUID());
    }
}
