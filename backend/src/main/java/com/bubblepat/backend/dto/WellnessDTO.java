package com.bubblepat.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class WellnessDTO {
    // Score general 0-100 calculado desde las actividades registradas
    private int score;
    // Etiqueta de estado: "Excelente", "Bien", "Atención", "Atrasado"
    private String level;
    private List<WellnessItem> items;
}
