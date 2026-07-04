package com.bubblepat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "routines")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @JsonIgnore
    private Pet pet;

    @Column(nullable = false)
    private String type; // "feeding", "walk", "water", "medicine", "bath", "other"

    private String description;

    // Horario (formato "HH:mm", ej. "08:00"). Opcional: adaptable al cuidador.
    private String startTime;
    private String endTime;

    // Días de la semana en que aplica (3 letras: "MON,TUE,WED,THU,FRI,SAT,SUN").
    // null o vacío = aplica todos los días.
    private String daysOfWeek;

    private LocalDateTime completedAt;
    private boolean completed = false;
}
