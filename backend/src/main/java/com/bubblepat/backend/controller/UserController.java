package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.ThemeRequest;
import com.bubblepat.backend.dto.UserProfileResponse;
import com.bubblepat.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@RequestAttribute("email") String email) {
        return ResponseEntity.ok(userService.getMe(email));
    }

    @PutMapping("/me/theme")
    public ResponseEntity<UserProfileResponse> updateTheme(@Valid @RequestBody ThemeRequest request,
                                                           @RequestAttribute("email") String email) {
        return ResponseEntity.ok(userService.updateThemeHue(email, request.getHue()));
    }
}
