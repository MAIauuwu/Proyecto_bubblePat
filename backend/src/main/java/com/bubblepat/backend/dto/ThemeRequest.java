package com.bubblepat.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ThemeRequest {
    @Min(value = 0, message = "El tono mínimo es 0")
    @Max(value = 360, message = "El tono máximo es 360")
    private Integer hue;
}
