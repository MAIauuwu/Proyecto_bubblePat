package com.bubblepat.backend.dto;

import lombok.Data;

@Data
public class BadgeDTO {
    private String key;
    private String label;
    private String emoji;
    private boolean earned;
    private String description;

    public BadgeDTO(String key, String label, String emoji, boolean earned, String description) {
        this.key = key;
        this.label = label;
        this.emoji = emoji;
        this.earned = earned;
        this.description = description;
    }
}
