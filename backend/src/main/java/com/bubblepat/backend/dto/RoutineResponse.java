package com.bubblepat.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoutineResponse {
    private Long id;
    private String type;
    private String description;
    private LocalDateTime completedAt;
    private boolean completed;
    private boolean doneToday;
}
