package de.mosesonline.http.adapter.backend.third;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.model.BackendData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.concurrent.CompletableFuture;

@BackendService(backendId = "third")
@ConditionalOnProperty(name="backend.third.enabled", havingValue = "true", matchIfMissing = true)
class ThirdBackendService  implements BackendPort {
    @Override
    public CompletableFuture<BackendData> fetchBackendData(String testCase) {
        return null;
    }
}
