package com.bubblepat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoutineRequest {
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    private String description;
    private String startTime;   // "HH:mm"
    private String endTime;     // "HH:mm"
    private String daysOfWeek;  // "MON,TUE,..." o null = todos los días
}
