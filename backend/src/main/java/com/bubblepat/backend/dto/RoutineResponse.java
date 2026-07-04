package com.bubblepat.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoutineResponse {
    private Long id;
    private String type;
    private String typeLabel;   // Etiqueta en español (Alimentación, Paseo, ...)
    private String description;
    private String startTime;   // "HH:mm"
    private String endTime;     // "HH:mm"
    private String daysOfWeek;  // "MON,TUE,..."
    private boolean appliesToday; // ¿Aplica el día de hoy según daysOfWeek?
    private LocalDateTime completedAt;
    private boolean completed;
    private boolean doneToday;
}
