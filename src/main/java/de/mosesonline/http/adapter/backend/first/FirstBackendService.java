package de.mosesonline.http.adapter.backend.first;

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


@QualifiedBackendService(backendId = "first")
class FirstBackendService implements BackendPort, SessionBackendPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirstBackendService.class);
    private final FirstBackendClient firstBackendClient;
    private final FirstModelMapper firstModelMapper;

    FirstBackendService(FirstBackendClient firstBackendClient, FirstModelMapper firstModelMapper) {
        this.firstBackendClient = firstBackendClient;
        this.firstModelMapper = firstModelMapper;
        LOGGER.info("First backend service created");
    }

    @TimeLimiter(name = "first-backend-service")
    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {
        LOGGER.info("first fetchBackendData");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return firstModelMapper.map(firstBackendClient.callFirst(testCase));
            } finally {
                LOGGER.info("request finished");
            }
        });
    }

    @Override
    public UserData requestUserData(UUID id) {
        return new UserData(id, "first " + UUID.randomUUID());
    }
}
