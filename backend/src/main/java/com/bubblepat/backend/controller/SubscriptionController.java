package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.UserProfileResponse;
import com.bubblepat.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final UserService userService;

    public SubscriptionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getPlan(@RequestAttribute("email") String email) {
        UserProfileResponse profile = userService.getMe(email);
        return ResponseEntity.ok(Map.of("plan", profile.getPlan() != null ? profile.getPlan() : "FREE"));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updatePlan(@RequestBody Map<String, String> body,
                                                          @RequestAttribute("email") String email) {
        return ResponseEntity.ok(userService.updatePlan(email, body.get("plan")));
    }
}
