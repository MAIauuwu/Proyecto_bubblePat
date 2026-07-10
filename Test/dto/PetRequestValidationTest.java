package com.bubblepat.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private PetRequest baseValido() {
        PetRequest r = new PetRequest();
        r.setName("Luna");
        r.setSpecies("Perro");
        return r;
    }

    @Test
    void requestValido_noGeneraViolaciones() {
        assertTrue(validator.validate(baseValido()).isEmpty());
    }

    @Test
    void nameVacio_esInvalido() {
        PetRequest r = baseValido();
        r.setName("");
        assertFalse(validator.validateProperty(r, "name").isEmpty());
    }

    @Test
    void nameNulo_esInvalido() {
        PetRequest r = baseValido();
        r.setName(null);
        assertFalse(validator.validateProperty(r, "name").isEmpty());
    }

    @Test
    void speciesEnBlanco_esInvalido() {
        PetRequest r = baseValido();
        r.setSpecies("   ");
        assertFalse(validator.validateProperty(r, "species").isEmpty());
    }

    @Test
    void speciesNulo_esInvalido() {
        PetRequest r = baseValido();
        r.setSpecies(null);
        assertFalse(validator.validateProperty(r, "species").isEmpty());
    }

    @Test
    void camposOpcionales_puedenSerNulos() {
        PetRequest r = new PetRequest();
        r.setName("Michi");
        r.setSpecies("Gato");
        // breed, birthDate, weight, allergicTo, lastDeworming son opcionales
        assertTrue(validator.validate(r).isEmpty());
    }
}
