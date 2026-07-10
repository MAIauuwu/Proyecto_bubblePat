package com.bubblepat.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReminderModelTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Pet pet = new Pet();
        pet.setName("Luna");

        Reminder rem = new Reminder();
        rem.setId(1L);
        rem.setPet(pet);
        rem.setTitle("Cita veterinaria");
        rem.setDescription("Vacuna anual");
        rem.setReminderDate(LocalDateTime.of(2026, 6, 20, 9, 0));
        rem.setCompleted(true);
        rem.setSource("VACCINE:5");

        assertEquals(1L, rem.getId());
        assertEquals("Luna", rem.getPet().getName());
        assertEquals("Cita veterinaria", rem.getTitle());
        assertEquals("Vacuna anual", rem.getDescription());
        assertEquals(LocalDateTime.of(2026, 6, 20, 9, 0), rem.getReminderDate());
        assertTrue(rem.isCompleted());
        assertEquals("VACCINE:5", rem.getSource());
    }

    @Test
    void valoresPorDefecto() {
        Reminder rem = new Reminder();
        assertFalse(rem.isCompleted());
        assertNull(rem.getSource());
    }

    @Test
    void sourceNull_esRecordatorioManual() {
        Reminder rem = new Reminder();
        rem.setSource(null);
        assertNull(rem.getSource());
    }

    @Test
    void sourceVaccine_esRecordatorioAutomatico() {
        Reminder rem = new Reminder();
        rem.setSource("VACCINE:10");
        assertEquals("VACCINE:10", rem.getSource());
    }
}