package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.model.BackendData;
import de.mosesonline.http.model.UserSessionData;
import de.mosesonline.http.routing.RequestRouterService;
import de.mosesonline.http.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/test")
class IncomingDataController {
    private final RequestRouterService backendService;
    private final UserService userService;

    IncomingDataController(RequestRouterService backendService, UserService userService) {
        this.backendService = backendService;
        this.userService = userService;
    }


    @GetMapping
    BackendData requestSimpleData(@RequestParam(required = false) String testCase) {
        return backendService.simpleObjectRequest(testCase);
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
