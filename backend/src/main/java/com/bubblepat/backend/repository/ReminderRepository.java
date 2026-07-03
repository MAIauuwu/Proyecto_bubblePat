package com.bubblepat.backend.repository;

import com.bubblepat.backend.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByPetId(Long petId);

    List<Reminder> findByPetIdOrderByReminderDateAsc(Long petId);

    // Recordatorios generados desde una vacuna ("VACCINE:<id>")
    List<Reminder> findBySource(String source);
}
