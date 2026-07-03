package com.bubblepat.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationResponse {
    private Long id;
    private String name;
    private LocalDate appliedDate;
    private LocalDate nextDoseDate;
    private String vetName;
    private String notes;
    // "al_dia", "por_vencer" (próx. dosis en <=30 días), "vencida" (próx. dosis en el pasado), "sin_proxima"
    private String status;
    private long daysUntilNext;
}
