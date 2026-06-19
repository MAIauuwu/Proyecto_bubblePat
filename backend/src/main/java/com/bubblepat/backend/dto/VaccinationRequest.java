package com.bubblepat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private LocalDate appliedDate;
    private LocalDate nextDoseDate;
    private String vetName;
    private String notes;
}
