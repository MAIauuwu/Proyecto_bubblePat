package com.bubblepat.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PetResponse {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private Double weight;
    private String allergicTo;
    private LocalDate lastDeworming;
    private int dailyStreak;
    private int bestStreak;
    private LocalDate lastRoutineDate;
    // Estado de la racha calculado al responder:
    // "done_today" (rutina de hoy completada), "active" (seguía viva, falta hoy),
    // "broken" (se rompió por inactividad) o "never" (nunca se ha registrado)
    private String streakStatus;
    private boolean routineDoneToday;
    // Fecha de inicio de la racha CALCULADA (no editable) y días que la componen (solo lectura)
    private LocalDate streakStartDate;
    private List<LocalDate> streakDays;
    private WellnessDTO wellness;
    private List<ActivityLogResponse> activityLog;
    private List<BadgeDTO> badges;
    private List<RoutineResponse> routines;
    private List<VaccinationResponse> vaccinations;
    private List<ReminderResponse> reminders;
}
