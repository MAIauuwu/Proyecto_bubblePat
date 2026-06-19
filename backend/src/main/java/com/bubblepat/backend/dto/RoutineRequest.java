package com.bubblepat.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoutineRequest {
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    private String description;
}
