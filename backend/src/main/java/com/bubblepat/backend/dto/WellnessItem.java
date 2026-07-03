package com.bubblepat.backend.dto;

import lombok.Data;

@Data
public class WellnessItem {
    private String key;
    private String label;
    private String icon;
    // "ok" (al día), "warning" (parcial/pendiente), "bad" (atrasado/olvidado)
    private String status;
    private String detail;

    public WellnessItem(String key, String label, String icon, String status, String detail) {
        this.key = key;
        this.label = label;
        this.icon = icon;
        this.status = status;
        this.detail = detail;
    }
}
