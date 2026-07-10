package com.bubblepat.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void gettersYSetters_deLombok_funcionanCorrectamente() {
        User user = new User();
        user.setEmail("mau@bubblepat.com");
        user.setPassword("secreto");
        user.setName("Mau");

        assertEquals("mau@bubblepat.com", user.getEmail());
        assertEquals("secreto", user.getPassword());
        assertEquals("Mau", user.getName());
    }

    @Test
    void equals_yHashCode_coincidenParaMismosValores() {
        User a = new User();
        a.setId(1L);
        a.setEmail("x@bubblepat.com");

        User b = new User();
        b.setId(1L);
        b.setEmail("x@bubblepat.com");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_esFalseParaObjetosDistintos() {
        User a = new User();
        a.setId(1L);

        User b = new User();
        b.setId(2L);

        assertNotEquals(a, b);
    }

    @Test
    void prePersist_asignaCreatedAtAlDiaDeHoy() {
        User user = new User();
        assertNull(user.getCreatedAt());

        user.onCreate();

        assertEquals(LocalDate.now(), user.getCreatedAt());
    }

    @Test
    void listaDePets_seInicializaVacia() {
        User user = new User();
        assertNotNull(user.getPets());
        assertTrue(user.getPets().isEmpty());
    }
}