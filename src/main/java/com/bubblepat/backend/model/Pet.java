package com.bubblepat.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;

    // Ficha medica
    private Double weight;
    private String allergicTo;
    private LocalDate lastDeworming;

    // Sistema de rachas
    private int dailyStreak = 0;
    private LocalDate lastRoutineDate;
}


