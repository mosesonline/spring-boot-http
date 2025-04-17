package de.mosesonline.http.routing;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.exception.BackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


@Service
public class RequestRouterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRouterService.class);

    private final BackendPort backendService;

    RequestRouterService(BackendPort backendService) {
        this.backendService = backendService;
    }

    @Reflective
    public BackendData simpleObjectRequest(String testCase) {
        try {
            LOGGER.info("Requesting test case {} to {}", testCase, backendService.getClass());
            return backendService.fetchBackendData(testCase).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while requesting test case {}", e.getMessage(), e);
            throw new BackendException(e);
        } catch (RuntimeException e) {
            LOGGER.error("Error while requesting test case {}", e.getMessage(), e);
            throw e;
        }
    }
}
