package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.AuthResponse;
import com.bubblepat.backend.dto.LoginRequest;
import com.bubblepat.backend.dto.RegisterRequest;
import com.bubblepat.backend.model.User;
import com.bubblepat.backend.repository.UserRepository;
import com.bubblepat.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    private RegisterRequest registroValido() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Mau");
        req.setEmail("mau@bubblepat.com");
        req.setPassword("123456");
        return req;
    }

    // ===================== REGISTER =====================

    @Test
    void register_emailNuevo_creaUsuarioYDevuelveToken() {
        when(userRepository.existsByEmail("mau@bubblepat.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken("mau@bubblepat.com")).thenReturn("jwt-token");

        AuthResponse resp = authService.register(registroValido());

        assertEquals("jwt-token", resp.getToken());
        assertEquals("mau@bubblepat.com", resp.getEmail());
        assertEquals("Mau", resp.getName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hash", captor.getValue().getPassword());
    }

    @Test
    void register_emailExistente_lanzaExcepcion() {
        when(userRepository.existsByEmail("mau@bubblepat.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registroValido()));
        assertEquals("El email ya está registrado", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ===================== LOGIN =====================

    @Test
    void login_credencialesCorrectas_devuelveToken() {
        User user = new User();
        user.setEmail("mau@bubblepat.com");
        user.setPassword("hash");
        user.setName("Mau");

        when(userRepository.findByEmail("mau@bubblepat.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("mau@bubblepat.com")).thenReturn("jwt-token");

        LoginRequest req = new LoginRequest();
        req.setEmail("mau@bubblepat.com");
        req.setPassword("123456");

        AuthResponse resp = authService.login(req);

        assertEquals("jwt-token", resp.getToken());
        assertEquals("Mau", resp.getName());
    }

    @Test
    void login_usuarioNoExiste_lanzaExcepcion() {
        when(userRepository.findByEmail("nadie@bubblepat.com")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setEmail("nadie@bubblepat.com");
        req.setPassword("123456");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void login_passwordIncorrecta_lanzaExcepcion() {
        User user = new User();
        user.setEmail("mau@bubblepat.com");
        user.setPassword("hash");

        when(userRepository.findByEmail("mau@bubblepat.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setEmail("mau@bubblepat.com");
        req.setPassword("mala");

        assertThrows(RuntimeException.class, () -> authService.login(req));
        verify(jwtUtil, never()).generateToken(any());
    }

    // ===================== GET BY EMAIL =====================

    @Test
    void getByEmail_usuarioExistente_loDevuelve() {
        User user = new User();
        user.setEmail("mau@bubblepat.com");
        when(userRepository.findByEmail("mau@bubblepat.com")).thenReturn(Optional.of(user));

        User encontrado = authService.getByEmail("mau@bubblepat.com");
        assertEquals("mau@bubblepat.com", encontrado.getEmail());
    }

    @Test
    void getByEmail_noExistente_lanza() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.getByEmail("x@x.com"));
    }
}
