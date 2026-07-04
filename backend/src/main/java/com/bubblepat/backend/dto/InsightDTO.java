package com.bubblepat.backend.dto;

import lombok.Data;

@Data
public class InsightDTO {
    // Tipo: "praise" (felicitacion), "alert" (urgente), "warning" (atencion),
    // "info" (neutral) o "tip" (consejo de cuidado).
    private String type;
    private String icon;
    private String title;
    private String message;

    public InsightDTO(String type, String icon, String title, String message) {
        this.type = type;
        this.icon = icon;
        this.title = title;
        this.message = message;
    }
}
