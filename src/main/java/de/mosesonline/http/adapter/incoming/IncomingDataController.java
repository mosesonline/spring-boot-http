package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.UserSessionData;
import de.mosesonline.http.routing.RequestRouterService;
import de.mosesonline.http.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/test")
class IncomingDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingDataController.class);
    private final RequestRouterService requestRouterService;
    private final UserService userService;

    IncomingDataController(RequestRouterService requestRouterService, UserService userService) {
        this.requestRouterService = requestRouterService;
        this.userService = userService;
    }


    @GetMapping
    BackendData requestSimpleData(@RequestParam(required = false) String testCase) {
        LOGGER.info("Test case: {} delegating to {}", testCase, requestRouterService);
        return requestRouterService.simpleObjectRequest(testCase);
    }

    @GetMapping(path = "users/{id}")
    UserSessionData getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping(path = "users-session")
    UserSessionData getUserSession() {
        return userService.getUserSession();
    }

}
