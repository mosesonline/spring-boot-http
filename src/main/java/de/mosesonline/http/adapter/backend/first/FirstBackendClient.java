package de.mosesonline.http.adapter.backend.first;

import de.mosesonline.http.adapter.backend.first.model.FirstBackendData;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

interface FirstBackendClient {
    @GetExchange("/test/first")
    FirstBackendData callFirst(@RequestParam(required = false) String testCase);
}
