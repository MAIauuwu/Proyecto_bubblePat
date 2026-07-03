package com.bubblepat.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {
    private Long id;
    private String type;
    private String title;
    private String icon;
    private LocalDateTime createdAt;
}
