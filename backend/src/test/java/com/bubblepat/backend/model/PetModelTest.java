package com.bubblepat.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PetModelTest {

    @Test
    void gettersYSetters_deLombok_funcionanCorrectamente() {
        Pet pet = new Pet();
        pet.setName("Rex");
        pet.setSpecies("Perro");
        pet.setBreed("Labrador");
        pet.setBirthDate(LocalDate.of(2020, 1, 15));
        pet.setWeight(25.5);
        pet.setAllergicTo("Pollo");
        pet.setLastDeworming(LocalDate.of(2026, 1, 1));

        assertEquals("Rex", pet.getName());
        assertEquals("Perro", pet.getSpecies());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(LocalDate.of(2020, 1, 15), pet.getBirthDate());
        assertEquals(25.5, pet.getWeight());
        assertEquals("Pollo", pet.getAllergicTo());
        assertEquals(LocalDate.of(2026, 1, 1), pet.getLastDeworming());
    }

    @Test
    void rachaDiaria_seInicializaEnCero() {
        Pet pet = new Pet();
        assertEquals(0, pet.getDailyStreak());
        assertEquals(0, pet.getBestStreak());
        assertNull(pet.getLastRoutineDate());
    }

    @Test
    void setDailyStreak_actualizaTambienBestStreakCuandoCorresponde() {
        Pet pet = new Pet();
        pet.setDailyStreak(5);
        pet.setBestStreak(5);

        assertEquals(5, pet.getDailyStreak());
        assertEquals(5, pet.getBestStreak());
    }

    @Test
    void listasDeColecciones_seInicializanVacias() {
        Pet pet = new Pet();
        assertNotNull(pet.getRoutines());
        assertNotNull(pet.getVaccinations());
        assertNotNull(pet.getReminders());

        assertTrue(pet.getRoutines().isEmpty());
        assertTrue(pet.getVaccinations().isEmpty());
        assertTrue(pet.getReminders().isEmpty());
    }

    @Test
    void relacionConUser_seAsignaCorrectamente() {
        User user = new User();
        user.setEmail("dueno@bubblepat.com");

        Pet pet = new Pet();
        pet.setName("Michi");
        pet.setUser(user);

        assertEquals("dueno@bubblepat.com", pet.getUser().getEmail());
    }
}
