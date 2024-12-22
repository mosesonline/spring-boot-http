package de.mosesonline.http.adapter.backend.first;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.model.BackendData;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;


@BackendService(backendId = "first")
class FirstBackendService implements BackendPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirstBackendService.class);
    private final FirstBackendClient backendClient;
    private final FirstModelMapper mapper;

    FirstBackendService(FirstBackendClient backendClient, FirstModelMapper mapper) {
        this.backendClient = backendClient;
        this.mapper = mapper;
    }

    @TimeLimiter(name = "first-backend-service")
    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mapper.map(backendClient.callFirst(testCase));
            } finally {
                LOGGER.info("request finished");
            }
        });
    }
}
