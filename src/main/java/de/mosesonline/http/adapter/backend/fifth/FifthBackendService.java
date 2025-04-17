package de.mosesonline.http.adapter.backend.fifth;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.QualifiedBackendService;
import de.mosesonline.http.model.BackendData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@QualifiedBackendService(backendId = "fifth")
class FifthBackendService implements BackendPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(FifthBackendService.class);

    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {

        LOGGER.info("fifth fetchBackendData");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new BackendData("fifth", OffsetDateTime.now(), 12.34f, BackendData.BackendStatus.OK);
            } finally {
                LOGGER.info("request finished");
            }
        });
    }
}
