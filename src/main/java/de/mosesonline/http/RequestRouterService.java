package de.mosesonline.http;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.exception.BackendException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static de.mosesonline.http.BackendRoutingConfiguration.BACKEND_QUALIFIER;

@Service
public class RequestRouterService {

    private final BackendPort backendPort;

    RequestRouterService(@Qualifier(BACKEND_QUALIFIER) BackendPort backendPort) {
        this.backendPort = backendPort;
    }

    public BackendData simpleObjectRequest(String testCase) {
        try {
            return backendPort.fetchBackendData(testCase).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BackendException(e);
        }
    }
}
