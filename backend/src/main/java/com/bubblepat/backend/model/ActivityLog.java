package com.bubblepat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Línea de tiempo de actividad de una mascota: registra eventos automáticos
// (rutina completada, vacuna registrada, hito de racha, recordatorio, etc.)
// para que el usuario sienta que el sistema "trabaja para él".
@Entity
@Data
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @JsonIgnore
    private Pet pet;

    @Column(nullable = false)
    private String type; // ROUTINE, VACCINE, STREAK, REMINDER, WELCOME

    @Column(nullable = false)
    private String title;

    private String icon;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
