package de.mosesonline.http.adapter.backend.second;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.model.BackendData;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;


@BackendService(backendId = "second")
class SecondBackendService implements BackendPort {
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
}
