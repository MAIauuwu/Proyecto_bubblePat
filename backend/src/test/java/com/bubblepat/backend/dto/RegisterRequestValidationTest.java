package com.bubblepat.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private int violacionesDe(RegisterRequest req, String propiedad) {
        return (int) validator.validateProperty(req, propiedad).stream()
                .map(ConstraintViolation::getPropertyPath)
                .filter(p -> p.toString().equals(propiedad))
                .count();
    }

    @Test
    void requestValido_noGeneraViolaciones() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Mau");
        req.setEmail("mau@bubblepat.com");
        req.setPassword("123456");

        assertTrue(validator.validate(req).isEmpty());
    }

    @Test
    void emailVacio_esInvalido() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Mau");
        req.setEmail("");
        req.setPassword("123456");

        assertFalse(validator.validateProperty(req, "email").isEmpty());
    }

    @Test
    void emailMalFormado_esInvalido() {
        RegisterRequest req = new RequestBuilder().conDatosValidosMenosEmail("no-es-un-email");
        assertFalse(validator.validateProperty(req, "email").isEmpty());
    }

    @Test
    void nameEnBlanco_esInvalido() {
        RegisterRequest req = new RequestBuilder().conDatosValidosMenosName("  ");
        assertFalse(validator.validateProperty(req, "name").isEmpty());
    }

    @Test
    void passwordNula_esInvalida() {
        RegisterRequest req = new RequestBuilder().conDatosValidos();
        req.setPassword(null);
        assertFalse(validator.validateProperty(req, "password").isEmpty());
    }

    /* Helper para construir DTOs legibles en los tests */
    static class RequestBuilder {
        RegisterRequest conDatosValidos() {
            return conDatosValidosMenosEmail("mau@bubblepat.com");
        }

        RegisterRequest conDatosValidosMenosEmail(String email) {
            RegisterRequest req = new RegisterRequest();
            req.setName("Mau");
            req.setEmail(email);
            req.setPassword("123456");
            return req;
        }

        RegisterRequest conDatosValidosMenosName(String name) {
            RegisterRequest req = new RegisterRequest();
            req.setName(name);
            req.setEmail("mau@bubblepat.com");
            req.setPassword("123456");
            return req;
        }
    }
}