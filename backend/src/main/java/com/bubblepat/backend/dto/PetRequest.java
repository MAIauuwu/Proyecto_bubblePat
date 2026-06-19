package com.bubblepat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PetRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La especie es obligatoria")
    private String species;

    private String breed;
    private LocalDate birthDate;
    private Double weight;
    private String allergicTo;
    private LocalDate lastDeworming;
}
