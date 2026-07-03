package com.bubblepat.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime reminderDate;
    private boolean completed;
    // Estado relativo a hoy: "vencido", "hoy", "proximo", "futuro" o "sin_fecha"
    private String status;
    private long daysUntil;
    // true si fue generado automáticamente (ej. desde una vacuna)
    private boolean automatic;
}
