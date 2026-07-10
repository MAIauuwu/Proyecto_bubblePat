package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.AuthResponse;
import com.bubblepat.backend.dto.ChangePasswordRequest;
import com.bubblepat.backend.dto.UpdateProfileRequest;
import com.bubblepat.backend.dto.UserProfileResponse;
import com.bubblepat.backend.model.User;
import com.bubblepat.backend.repository.UserRepository;
import com.bubblepat.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserProfileResponse getMe(String email) {
        return toProfile(getByEmail(email));
    }

    public UserProfileResponse updateThemeHue(String email, Integer hue) {
        User user = getByEmail(email);
        user.setThemeHue(hue);
        userRepository.save(user);
        return toProfile(user);
    }

    public UserProfileResponse updatePlan(String email, String plan) {
        User user = getByEmail(email);
        user.setPlan(plan);
        userRepository.save(user);
        return toProfile(user);
    }

    // Actualiza nombre y correo. Devuelve un JWT nuevo (con el correo actualizado)
    // para que la sesión siga siendo válida tras cambiar el email.
    public AuthResponse updateProfile(String currentEmail, UpdateProfileRequest req) {
        User user = getByEmail(currentEmail);
        if (!req.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()),
                user.getEmail(), user.getName(), user.getThemeHue(), user.getPlan());
    }

    public void changePassword(String email, ChangePasswordRequest req) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    private User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UserProfileResponse toProfile(User u) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setThemeHue(u.getThemeHue());
        r.setPlan(u.getPlan());
        return r;
    }
}
