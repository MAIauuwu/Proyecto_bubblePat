package com.bubblepat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderRequest {
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    private LocalDateTime reminderDate;
}
