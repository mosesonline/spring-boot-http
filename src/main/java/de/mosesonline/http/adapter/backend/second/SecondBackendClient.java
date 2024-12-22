package de.mosesonline.http.adapter.backend.second;

import de.mosesonline.http.model.BackendData;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

interface SecondBackendClient {
    @GetExchange("/test/second")
    BackendData callSecond(@RequestParam(required = false) String testCase);
}
