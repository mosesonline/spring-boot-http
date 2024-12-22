package de.mosesonline.http.adapter.backend.fourth;

import de.mosesonline.http.model.BackendData;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

interface FourthBackendClient {
    @GetExchange("/test/fourth")
    BackendData callFourth(@RequestParam(required = false) String testCase);
}
