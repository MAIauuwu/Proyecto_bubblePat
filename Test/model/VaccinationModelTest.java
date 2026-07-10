package com.bubblepat.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VaccinationModelTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Pet pet = new Pet();
        pet.setName("Michi");

        Vaccination vac = new Vaccination();
        vac.setId(1L);
        vac.setPet(pet);
        vac.setName("Rabia");
        vac.setAppliedDate(LocalDate.of(2026, 1, 10));
        vac.setNextDoseDate(LocalDate.of(2027, 1, 10));
        vac.setVetName("Dr. House");
        vac.setNotes("Sin reacciones");

        assertEquals(1L, vac.getId());
        assertEquals("Michi", vac.getPet().getName());
        assertEquals("Rabia", vac.getName());
        assertEquals(LocalDate.of(2026, 1, 10), vac.getAppliedDate());
        assertEquals(LocalDate.of(2027, 1, 10), vac.getNextDoseDate());
        assertEquals("Dr. House", vac.getVetName());
        assertEquals("Sin reacciones", vac.getNotes());
    }

    @Test
    void camposOpcionales_seInicializanNulos() {
        Vaccination vac = new Vaccination();
        assertNull(vac.getAppliedDate());
        assertNull(vac.getNextDoseDate());
        assertNull(vac.getVetName());
        assertNull(vac.getNotes());
    }
}
