package com.bubblepat.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RoutineModelTest {

    @Test
    void gettersYSetters_funcionanCorrectamente() {
        Pet pet = new Pet();
        pet.setName("Rex");

        Routine r = new Routine();
        r.setId(1L);
        r.setPet(pet);
        r.setType("feeding");
        r.setDescription("Desayuno");
        r.setStartTime("08:00");
        r.setEndTime("08:30");
        r.setDaysOfWeek("MON,TUE,WED");
        r.setCompleted(true);
        r.setCompletedAt(LocalDateTime.of(2026, 7, 9, 8, 0));

        assertEquals(1L, r.getId());
        assertEquals("Rex", r.getPet().getName());
        assertEquals("feeding", r.getType());
        assertEquals("Desayuno", r.getDescription());
        assertEquals("08:00", r.getStartTime());
        assertEquals("08:30", r.getEndTime());
        assertEquals("MON,TUE,WED", r.getDaysOfWeek());
        assertTrue(r.isCompleted());
        assertEquals(LocalDateTime.of(2026, 7, 9, 8, 0), r.getCompletedAt());
    }

    @Test
    void completed_seInicializaEnFalse() {
        Routine r = new Routine();
        assertFalse(r.isCompleted());
        assertNull(r.getCompletedAt());
    }
}
