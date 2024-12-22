package de.mosesonline.http.api;

import de.mosesonline.http.model.BackendData;

import java.util.concurrent.CompletableFuture;

public interface BackendPort {
    CompletableFuture<BackendData> fetchBackendData(String testCase);
}
