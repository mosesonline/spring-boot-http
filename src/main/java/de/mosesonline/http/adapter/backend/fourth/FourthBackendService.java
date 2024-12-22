package de.mosesonline.http.adapter.backend.fourth;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.model.BackendData;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@BackendService(backendId = "fourth")
class FourthBackendService implements BackendPort {
    private final static Logger LOGGER = LoggerFactory.getLogger(FourthBackendService.class);
    private final FourthBackendClient fourthBackendClient;

    FourthBackendService(FourthBackendClient fourthBackendClient) {
        this.fourthBackendClient = fourthBackendClient;
    }

    @Retry(name = "fourth-backend-retry")
    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return fourthBackendClient.callFourth(testCase);
            } catch (RuntimeException e) {
                LOGGER.warn("request finished with exception", e);
                throw e;
            } finally {
                LOGGER.info("request finished");
            }
        });
    }
}
