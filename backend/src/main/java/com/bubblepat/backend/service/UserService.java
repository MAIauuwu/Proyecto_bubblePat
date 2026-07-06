package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.UserProfileResponse;
import com.bubblepat.backend.model.User;
import com.bubblepat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return r;
    }
}
