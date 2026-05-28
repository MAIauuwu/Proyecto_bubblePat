package com.bubblepat.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Modelo de recordatorio para cada mascota, para tener un registro de las tareas pendientes, como citas veterinarias, baños, etc.

@Entity
@Data
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(nullable = false)
    private String title;

    private String description;
    private LocalDateTime reminderDate;
    private boolean completed = false;
}