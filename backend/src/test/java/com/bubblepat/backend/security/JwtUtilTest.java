package com.bubblepat.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Secreto de al menos 32 bytes (256 bits) para HS256.
        String secret = "bubblepat-super-secret-key-2026-must-be-at-least-256-bits-long-for-hs256";
        long expiration = 86_400_000L; // 24h
        jwtUtil = new JwtUtil(secret, expiration);
    }

    @Test
    void generateToken_yExtractEmail_devuelvenElMismoEmail() {
        String token = jwtUtil.generateToken("user@bubblepat.com");

        assertNotNull(token);
        assertEquals("user@bubblepat.com", jwtUtil.extractEmail(token));
    }

    @Test
    void isTokenValid_tokenValido_retornaTrue() {
        String token = jwtUtil.generateToken("gato@bubblepat.com");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_tokenManipulado_retornaFalse() {
        String token = jwtUtil.generateToken("a@b.com") + "X";

        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_tokenExpirado_retornaFalse() throws InterruptedException {
        JwtUtil expirado = new JwtUtil(
                "bubblepat-super-secret-key-2026-must-be-at-least-256-bits-long-for-hs256",
                1L // 1 ms
        );
        String token = expirado.generateToken("x@y.com");

        Thread.sleep(20L);

        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void extractEmail_tokenConOtroSecreto_lanzaExcepcion() {
        JwtUtil otro = new JwtUtil(
                "otra-clave-super-secreta-y-muy-larga-para-pasar-la-validacion-de-256-bits",
                86_400_000L
        );
        String token = otro.generateToken("hola@bubblepat.com");

        // Como fue firmado con otra clave, al validarlo con jwtUtil debe fallar.
        assertFalse(jwtUtil.isTokenValid(token));
    }
}
