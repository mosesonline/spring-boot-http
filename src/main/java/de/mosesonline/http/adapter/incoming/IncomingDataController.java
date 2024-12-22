package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.RequestRouterService;
import de.mosesonline.http.model.BackendData;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
class IncomingDataController {
    private final RequestRouterService firstBackendService;

    IncomingDataController(RequestRouterService firstBackendService) {
        this.firstBackendService = firstBackendService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    BackendData requestSimpleData(@RequestParam(required = false) String testCase) {
        return firstBackendService.simpleObjectRequest(testCase);
    }

}
