package de.mosesonline.http.routing;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.exception.BackendException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


@Service
public class RequestRouterService {

    private final BackendPort backendService;

    RequestRouterService(BackendPort backendService) {
        this.backendService = backendService;
    }

    public BackendData simpleObjectRequest(String testCase) {
        try {
            return backendService.fetchBackendData(testCase).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BackendException(e);
        }
    }
}
