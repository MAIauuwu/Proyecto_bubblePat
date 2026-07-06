package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.AuthResponse;
import com.bubblepat.backend.dto.ChangePasswordRequest;
import com.bubblepat.backend.dto.ThemeRequest;
import com.bubblepat.backend.dto.UpdateProfileRequest;
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

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                      @RequestAttribute("email") String email) {
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               @RequestAttribute("email") String email) {
        userService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }
}
