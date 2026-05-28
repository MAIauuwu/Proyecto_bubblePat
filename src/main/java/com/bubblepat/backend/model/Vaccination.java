package com.bubblepat.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;



// Modelo de vacunación para cada mascota para tener registro de las vacunas aplicadas, fechas, veterinario, etc.

@Entity
@Data
@Table(name = "vaccinations")
public class Vaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(nullable = false)
    private String name;

    private LocalDate appliedDate;
    private LocalDate nextDoseDate;
    private String vetName;
    private String notes;
}