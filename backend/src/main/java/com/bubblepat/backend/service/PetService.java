package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.*;
import com.bubblepat.backend.model.ActivityLog;
import com.bubblepat.backend.model.Pet;
import com.bubblepat.backend.model.Routine;
import com.bubblepat.backend.model.Vaccination;
import com.bubblepat.backend.model.Reminder;
import com.bubblepat.backend.model.User;
import com.bubblepat.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final VaccinationRepository vaccinationRepository;
    private final ReminderRepository reminderRepository;
    private final ActivityLogRepository activityLogRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository,
                      RoutineRepository routineRepository, VaccinationRepository vaccinationRepository,
                      ReminderRepository reminderRepository, ActivityLogRepository activityLogRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.reminderRepository = reminderRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public List<PetResponse> listarPorUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getPets().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PetResponse obtenerPorId(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso para ver esta mascota");
        }
        return toResponse(pet);
    }

    public PetResponse crear(PetRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String plan = user.getPlan();
        if ((plan == null || plan.equals("FREE")) && user.getPets().size() >= 2) {
            throw new RuntimeException("Plan gratuito: máximo 2 mascotas. Mejora a Premium para mascotas ilimitadas.");
        }

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setBirthDate(request.getBirthDate());
        pet.setWeight(request.getWeight());
        pet.setAllergicTo(request.getAllergicTo());
        pet.setLastDeworming(request.getLastDeworming());
        pet.setSex(request.getSex());
        pet.setLastHeatDate(request.getLastHeatDate());
        pet.setUser(user);
        pet = petRepository.save(pet);

        registrarActividad(pet, "WELCOME", "🐾", "¡" + pet.getName() + " se unió a BubblePat!");
        return toResponse(pet);
    }

    public PetResponse actualizar(Long id, PetRequest request, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso");
        }

        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setBirthDate(request.getBirthDate());
        pet.setWeight(request.getWeight());
        pet.setAllergicTo(request.getAllergicTo());
        pet.setLastDeworming(request.getLastDeworming());
        pet.setSex(request.getSex());
        pet.setLastHeatDate(request.getLastHeatDate());

        return toResponse(petRepository.save(pet));
    }

    @Transactional
    public void eliminar(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso");
        }
        // Borrar primero la línea de tiempo: tiene FK a pets y no está en cascada,
        // de lo contrario el DELETE de la mascota falla por restricción de integridad.
        activityLogRepository.deleteByPetId(pet.getId());
        petRepository.delete(pet);
    }

    public PetResponse actualizarRacha(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso");
        }

        LocalDate today = LocalDate.now();
        if (today.equals(pet.getLastRoutineDate())) {
            throw new RuntimeException("Ya completaste la rutina de hoy");
        }

        // Marcar todas las rutinas de hoy como completadas para que el detalle
        // refleje el mismo estado que el dashboard.
        List<Routine> rutinas = routineRepository.findByPetId(pet.getId());
        for (Routine r : rutinas) {
            if (aplicaHoy(r, today) && !(r.isCompleted() && r.getCompletedAt() != null
                    && r.getCompletedAt().toLocalDate().equals(today))) {
                r.setCompleted(true);
                r.setCompletedAt(LocalDateTime.now());
                routineRepository.save(r);
            }
        }

        int antes = pet.getDailyStreak();
        aplicarAvanceRacha(pet, today);
        verificarHitoRacha(pet, antes, pet.getDailyStreak());
        return toResponse(pet);
    }

    // Calcula y persiste el avance de la racha para 'today' (sin validaciones de guarda).
    private void aplicarAvanceRacha(Pet pet, LocalDate today) {
        LocalDate last = pet.getLastRoutineDate();
        int newStreak;
        if (last == null) {
            newStreak = 1;
        } else if (last.equals(today.minusDays(1))) {
            newStreak = pet.getDailyStreak() + 1;
        } else {
            newStreak = 1;
        }
        pet.setDailyStreak(newStreak);
        pet.setLastRoutineDate(today);
        if (newStreak > pet.getBestStreak()) {
            pet.setBestStreak(newStreak);
        }
        petRepository.save(pet);
    }

    // Sincroniza la racha con el estado REAL de las rutinas de hoy:
    //  - si TODAS están hechas hoy y hoy no estaba contado -> avanza (+1)
    //  - si FALTA alguna y hoy SÍ estaba contado -> revierte (hoy deja de contar)
    private void sincronizarRacha(Pet pet, LocalDate today) {
        List<Routine> rutinas = routineRepository.findByPetId(pet.getId());
        if (rutinas.isEmpty()) return; // sin rutinas no hay racha
        // Solo cuentan las rutinas programadas para hoy (según daysOfWeek).
        List<Routine> deHoy = rutinas.stream().filter(r -> aplicaHoy(r, today)).collect(Collectors.toList());
        if (deHoy.isEmpty()) return;
        boolean todasHoy = deHoy.stream().allMatch(r ->
                r.isCompleted() && r.getCompletedAt() != null
                        && r.getCompletedAt().toLocalDate().equals(today));
        if (todasHoy) {
            if (!today.equals(pet.getLastRoutineDate())) {
                int antes = pet.getDailyStreak();
                aplicarAvanceRacha(pet, today);
                verificarHitoRacha(pet, antes, pet.getDailyStreak());
            }
        } else if (today.equals(pet.getLastRoutineDate())) {
            revertirRacha(pet, today);
        }
    }

    // Descontar el día de hoy de la racha (p.ej. al agregar una rutina pendiente
    // después de que hoy ya se había completado todo).
    private void revertirRacha(Pet pet, LocalDate today) {
        if (pet.getDailyStreak() >= 2) {
            pet.setDailyStreak(pet.getDailyStreak() - 1);
            pet.setLastRoutineDate(today.minusDays(1));
        } else {
            pet.setDailyStreak(0);
            pet.setLastRoutineDate(null);
        }
        petRepository.save(pet);
    }

    // Registra un hito de racha en la línea de tiempo (3, 7, 14, 30, 60, 100 días).
    private void verificarHitoRacha(Pet pet, int antes, int despues) {
        Set<Integer> hitos = new HashSet<>(Arrays.asList(3, 7, 14, 30, 60, 100));
        for (int h : hitos) {
            if (antes < h && despues >= h) {
                registrarActividad(pet, "STREAK", "🔥",
                        "¡Racha de " + h + " día" + (h == 1 ? "" : "s") + "!");
            }
        }
    }

    // Indica si una rutina aplica el día dado según su daysOfWeek
    // (null/vacío = todos los días; "MON,TUE,..." = solo esos días).
    private boolean aplicaHoy(Routine r, LocalDate today) {
        String d = r.getDaysOfWeek();
        if (d == null || d.isBlank()) return true;
        String hoy3 = today.getDayOfWeek().name().substring(0, 3); // MON, TUE, ...
        return Arrays.asList(d.split(",")).contains(hoy3);
    }

    // === Línea de tiempo ===
    private void registrarActividad(Pet pet, String type, String icon, String title) {
        try {
            ActivityLog log = new ActivityLog();
            log.setPet(pet);
            log.setType(type);
            log.setIcon(icon);
            log.setTitle(title);
            activityLogRepository.save(log);
        } catch (Exception ignored) {
            // La línea de tiempo es complementaria: no debe romper la operación principal.
        }
    }

    // === RUTINAS ===
    public RoutineResponse agregarRutina(Long petId, RoutineRequest request, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");

        Routine routine = new Routine();
        routine.setPet(pet);
        routine.setType(request.getType());
        routine.setDescription(request.getDescription());
        routine.setStartTime(request.getStartTime());
        routine.setEndTime(request.getEndTime());
        routine.setDaysOfWeek(request.getDaysOfWeek());
        routine = routineRepository.save(routine);

        // Si hoy ya estaba toda la racha completada, agregar una rutina pendiente
        // hace que hoy vuelva a estar incompleto: se descuenta el día.
        sincronizarRacha(pet, LocalDate.now());

        return toRoutineResponse(routine);
    }

    public List<RoutineResponse> listarRutinas(Long petId, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        return routineRepository.findByPetId(petId).stream().map(this::toRoutineResponse).collect(Collectors.toList());
    }

    public RoutineResponse completarRutina(Long routineId, String email) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        if (!routine.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");

        LocalDate today = LocalDate.now();
        routine.setCompleted(true);
        routine.setCompletedAt(LocalDateTime.now());
        routine = routineRepository.save(routine);

        Pet pet = routine.getPet();

        // Sincroniza la racha: avanza si con esta rutina quedan TODAS hechas hoy.
        int antes = pet.getDailyStreak();
        sincronizarRacha(pet, today);
        verificarHitoRacha(pet, antes, pet.getDailyStreak());

        // Línea de tiempo: el sistema "trabaja para el usuario".
        String tipo = etiquetaTipoRutina(routine.getType());
        registrarActividad(pet, "ROUTINE", iconoTipoRutina(routine.getType()),
                "Completaste \"" + tipo + "\"" + (routine.getDescription() != null && !routine.getDescription().isBlank()
                        ? " · " + routine.getDescription() : ""));

        return toRoutineResponse(routine);
    }

    public RoutineResponse editarRutina(Long routineId, RoutineRequest request, String email) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        if (!routine.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        routine.setType(request.getType());
        routine.setDescription(request.getDescription());
        routine.setStartTime(request.getStartTime());
        routine.setEndTime(request.getEndTime());
        routine.setDaysOfWeek(request.getDaysOfWeek());
        return toRoutineResponse(routineRepository.save(routine));
    }

    public void eliminarRutina(Long routineId, String email) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        if (!routine.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        Pet pet = routine.getPet();
        routineRepository.delete(routine);
        // Al eliminar una rutina, hoy podría quedar completo -> avanza la racha.
        sincronizarRacha(pet, LocalDate.now());
    }

    // === VACUNAS ===
    public VaccinationResponse agregarVacuna(Long petId, VaccinationRequest request, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");

        Vaccination vac = new Vaccination();
        vac.setPet(pet);
        vac.setName(request.getName());
        vac.setAppliedDate(request.getAppliedDate());
        vac.setNextDoseDate(request.getNextDoseDate());
        vac.setVetName(request.getVetName());
        vac.setNotes(request.getNotes());
        vac = vaccinationRepository.save(vac);

        // Automatización: si hay próxima dosis, se crea/actualiza el recordatorio vinculado.
        sincronizarRecordatorioVacuna(pet, vac);

        registrarActividad(pet, "VACCINE", "💉", "Vacuna \"" + vac.getName() + "\" registrada");
        return toVaccinationResponse(vac);
    }

    public List<VaccinationResponse> listarVacunas(Long petId, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        return vaccinationRepository.findByPetId(petId).stream().map(this::toVaccinationResponse).collect(Collectors.toList());
    }

    public VaccinationResponse editarVacuna(Long vaccinationId, VaccinationRequest request, String email) {
        Vaccination vac = vaccinationRepository.findById(vaccinationId)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        if (!vac.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        vac.setName(request.getName());
        vac.setAppliedDate(request.getAppliedDate());
        vac.setNextDoseDate(request.getNextDoseDate());
        vac.setVetName(request.getVetName());
        vac.setNotes(request.getNotes());
        vac = vaccinationRepository.save(vac);

        // Mantener el recordatorio vinculado sincronizado con la nueva info.
        sincronizarRecordatorioVacuna(vac.getPet(), vac);
        return toVaccinationResponse(vac);
    }

    public void eliminarVacuna(Long vaccinationId, String email) {
        Vaccination vac = vaccinationRepository.findById(vaccinationId)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        if (!vac.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        // Borrar también el recordatorio automático asociado (si existe).
        reminderRepository.findBySource(sourceVacuna(vac.getId())).forEach(reminderRepository::delete);
        vaccinationRepository.delete(vac);
    }

    // Crea/actualiza/elimina el recordatorio automático "Próxima dosis" de una vacuna.
    private void sincronizarRecordatorioVacuna(Pet pet, Vaccination vac) {
        String source = sourceVacuna(vac.getId());
        List<Reminder> existentes = reminderRepository.findBySource(source);

        if (vac.getNextDoseDate() != null) {
            LocalDateTime fecha = vac.getNextDoseDate().atTime(9, 0);
            Reminder rem;
            if (existentes.isEmpty()) {
                rem = new Reminder();
                rem.setPet(pet);
                rem.setSource(source);
                rem.setCompleted(false);
            } else {
                rem = existentes.get(0);
            }
            rem.setTitle("Próxima dosis: " + vac.getName());
            rem.setDescription("Recordatorio automático generado desde la ficha de vacunación.");
            rem.setReminderDate(fecha);
            reminderRepository.save(rem);
        } else {
            // Sin próxima dosis: el recordatorio automático ya no aplica.
            existentes.forEach(reminderRepository::delete);
        }
    }

    private String sourceVacuna(Long vaccinationId) {
        return "VACCINE:" + vaccinationId;
    }

    // === RECORDATORIOS ===
    public ReminderResponse agregarRecordatorio(Long petId, ReminderRequest request, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");

        Reminder reminder = new Reminder();
        reminder.setPet(pet);
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setReminderDate(request.getReminderDate());
        return toReminderResponse(reminderRepository.save(reminder));
    }

    public List<ReminderResponse> listarRecordatorios(Long petId, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        return reminderRepository.findByPetId(petId).stream().map(this::toReminderResponse).collect(Collectors.toList());
    }

    public ReminderResponse completarRecordatorio(Long reminderId, String email) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
        if (!reminder.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        reminder.setCompleted(true);
        reminder = reminderRepository.save(reminder);

        registrarActividad(reminder.getPet(), "REMINDER", "🔔", "Recordatorio \"" + reminder.getTitle() + "\" completado");
        return toReminderResponse(reminder);
    }

    public ReminderResponse editarRecordatorio(Long reminderId, ReminderRequest request, String email) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
        if (!reminder.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        reminder.setTitle(request.getTitle());
        reminder.setDescription(request.getDescription());
        reminder.setReminderDate(request.getReminderDate());
        return toReminderResponse(reminderRepository.save(reminder));
    }

    public void eliminarRecordatorio(Long reminderId, String email) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
        if (!reminder.getPet().getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        reminderRepository.delete(reminder);
    }

    // === MAPPERS (convierten Entity -> DTO Response) ===
    private PetResponse toResponse(Pet pet) {
        PetResponse r = new PetResponse();
        r.setId(pet.getId());
        r.setName(pet.getName());
        r.setSpecies(pet.getSpecies());
        r.setBreed(pet.getBreed());
        r.setBirthDate(pet.getBirthDate());
        r.setWeight(pet.getWeight());
        r.setAllergicTo(pet.getAllergicTo());
        r.setLastDeworming(pet.getLastDeworming());
        r.setSex(pet.getSex());
        r.setLastHeatDate(pet.getLastHeatDate());

        // Cálculo de la racha efectiva: si ya está rota, se muestra en 0
        LocalDate today = LocalDate.now();
        LocalDate last = pet.getLastRoutineDate();
        String streakStatus;
        boolean doneToday;
        int effectiveStreak;
        if (last == null) {
            effectiveStreak = 0;
            streakStatus = "never";
            doneToday = false;
        } else if (last.equals(today)) {
            effectiveStreak = pet.getDailyStreak();
            streakStatus = "done_today";
            doneToday = true;
        } else if (last.equals(today.minusDays(1))) {
            effectiveStreak = pet.getDailyStreak();
            streakStatus = "active";
            doneToday = false;
        } else {
            effectiveStreak = 0;
            streakStatus = "broken";
            doneToday = false;
        }
        r.setDailyStreak(effectiveStreak);
        r.setBestStreak(pet.getBestStreak());
        r.setLastRoutineDate(last);
        r.setStreakStatus(streakStatus);
        r.setRoutineDoneToday(doneToday);

        // Días de racha (read-only): la fecha de inicio se CALCULA, no se guarda ni edita.
        // Así es imposible "marcar" que la racha empezó antes de lo real.
        if (effectiveStreak > 0 && last != null) {
            LocalDate inicio = last.minusDays(effectiveStreak - 1L);
            r.setStreakStartDate(inicio);
            List<LocalDate> dias = new ArrayList<>();
            for (int i = 0; i < effectiveStreak; i++) dias.add(inicio.plusDays(i));
            r.setStreakDays(dias);
        } else {
            r.setStreakStartDate(null);
            r.setStreakDays(Collections.emptyList());
        }

        List<Routine> rutinas = routineRepository.findByPetId(pet.getId());
        List<Vaccination> vacunas = vaccinationRepository.findByPetId(pet.getId());
        List<Reminder> recordatorios = reminderRepository.findByPetIdOrderByReminderDateAsc(pet.getId());

        r.setRoutines(rutinas.stream().map(this::toRoutineResponse).collect(Collectors.toList()));
        r.setVaccinations(vacunas.stream().map(this::toVaccinationResponse).collect(Collectors.toList()));
        r.setReminders(recordatorios.stream().map(this::toReminderResponse).collect(Collectors.toList()));

        // Indicador de bienestar y medallas (gamificación)
        r.setWellness(calcularWellness(pet, rutinas, vacunas, streakStatus));
        r.setBadges(calcularMedallas(pet, rutinas, vacunas, r.getWellness().getScore(),
                doneToday, todasRutinasHechasHoy(rutinas, today)));
        // Asistente BubblePat: mensajes inteligentes
        r.setInsights(calcularInsights(pet, rutinas, vacunas, recordatorios, streakStatus, effectiveStreak, doneToday));

        r.setActivityLog(activityLogRepository.findTop30ByPetIdOrderByCreatedAtDesc(pet.getId()).stream()
                .map(this::toActivityLogResponse).collect(Collectors.toList()));
        return r;
    }

    private boolean todasRutinasHechasHoy(List<Routine> rutinas, LocalDate today) {
        List<Routine> deHoy = rutinas.stream().filter(r -> aplicaHoy(r, today)).collect(Collectors.toList());
        if (deHoy.isEmpty()) return false;
        return deHoy.stream().allMatch(r -> r.isCompleted() && r.getCompletedAt() != null
                && r.getCompletedAt().toLocalDate().equals(today));
    }

    // === Bienestar: score 0-100 calculado desde actividades registradas ===
    private WellnessDTO calcularWellness(Pet pet, List<Routine> rutinas, List<Vaccination> vacunas, String streakStatus) {
        LocalDate today = LocalDate.now();
        List<WellnessItem> items = new ArrayList<>();

        // Categorías por tipo de rutina que la mascota tenga registradas y apliquen hoy.
        Map<String, List<Routine>> porTipo = rutinas.stream()
                .filter(r -> aplicaHoy(r, today)).collect(Collectors.groupingBy(Routine::getType));
        for (Map.Entry<String, List<Routine>> e : porTipo.entrySet()) {
            String tipo = e.getKey();
            if (tipo == null || tipo.equals("other")) continue; // "otro" no aporta al indicador
            List<Routine> lista = e.getValue();
            long hechasHoy = lista.stream().filter(r -> r.isCompleted() && r.getCompletedAt() != null
                    && r.getCompletedAt().toLocalDate().equals(today)).count();
            String status;
            String detail;
            if (hechasHoy == lista.size()) { status = "ok"; detail = "Al día"; }
            else if (hechasHoy > 0) { status = "warning"; detail = (hechasHoy + "/" + lista.size() + " hoy"); }
            else { status = "bad"; detail = "Pendiente hoy"; }
            items.add(new WellnessItem(tipo, etiquetaTipoRutina(tipo), iconoTipoRutina(tipo), status, detail));
        }

        // Vacunas: solo si la mascota tiene alguna registrada.
        if (!vacunas.isEmpty()) {
            long vencidas = vacunas.stream().filter(v -> v.getNextDoseDate() != null && v.getNextDoseDate().isBefore(today)).count();
            long porVencer = vacunas.stream().filter(v -> v.getNextDoseDate() != null
                    && !v.getNextDoseDate().isBefore(today) && v.getNextDoseDate().isBefore(today.plusDays(31))).count();
            String status;
            String detail;
            if (vencidas > 0) { status = "bad"; detail = vencidas + " vencida" + (vencidas == 1 ? "" : "s"); }
            else if (porVencer > 0) { status = "warning"; detail = "Próxima dosis cerca"; }
            else { status = "ok"; detail = "Al día"; }
            items.add(new WellnessItem("vaccines", "Vacunas", "💉", status, detail));
        }

        // Felicidad: reflejada por la racha actual.
        String felicidadStatus;
        String felicidadDetail;
        if ("done_today".equals(streakStatus)) { felicidadStatus = "ok"; felicidadDetail = "¡Rutina de hoy lista!"; }
        else if ("active".equals(streakStatus)) { felicidadStatus = "ok"; felicidadDetail = "Racha activa"; }
        else if ("broken".equals(streakStatus)) { felicidadStatus = "warning"; felicidadDetail = "Racha rota"; }
        else { felicidadStatus = "warning"; felicidadDetail = "Aún sin racha"; }
        items.add(new WellnessItem("happiness", "Felicidad", "😊", felicidadStatus, felicidadDetail));

        // Score base = promedio ponderado (ok=1, warning=0.5, bad=0)
        double suma = 0;
        for (WellnessItem it : items) {
            suma += "ok".equals(it.getStatus()) ? 1.0 : ("warning".equals(it.getStatus()) ? 0.5 : 0.0);
        }
        int baseScore = items.isEmpty() ? 0 : (int) Math.round(suma / items.size() * 100.0);

        // Factor de completitud del perfil: penaliza datos faltantes
        int checks = 0, passed = 0;
        if (pet.getWeight() != null) passed++; checks++;
        if (pet.getBirthDate() != null) passed++; checks++;
        if (!rutinas.isEmpty()) passed++; checks++;
        if (!vacunas.isEmpty()) passed++; checks++;
        double completeness = checks > 0 ? (double) passed / checks : 0;

        int score = (int) Math.round(baseScore * completeness);
        String level = score >= 85 ? "Excelente" : score >= 60 ? "Bien" : score >= 35 ? "Atención" : "Atrasado";

        WellnessDTO w = new WellnessDTO();
        w.setScore(score);
        w.setLevel(level);
        w.setItems(items);
        return w;
    }

    // === Medallas dinámicas (gamificación) ===
    private List<BadgeDTO> calcularMedallas(Pet pet, List<Routine> rutinas, List<Vaccination> vacunas,
                                            int wellnessScore, boolean doneToday, boolean todasHoy) {
        LocalDate today = LocalDate.now();
        long vacunasVencidas = vacunas.stream().filter(v -> v.getNextDoseDate() != null && v.getNextDoseDate().isBefore(today)).count();
        boolean vacunasAlDia = !vacunas.isEmpty() && vacunasVencidas == 0;

        List<BadgeDTO> badges = new ArrayList<>();
        badges.add(new BadgeDTO("welcome", "Bienvenida", "🐾", true, "Registraste a tu mascota"));
        badges.add(new BadgeDTO("first_routine", "Primer paso", "⭐", !rutinas.isEmpty(), "Agrega tu primera rutina"));
        badges.add(new BadgeDTO("first_vaccine", "Vacunado", "💉", !vacunas.isEmpty(), "Registra una vacuna"));
        badges.add(new BadgeDTO("streak_3", "En racha", "🔥", pet.getBestStreak() >= 3, "Alcanza 3 días de racha"));
        badges.add(new BadgeDTO("streak_7", "Semana perfecta", "🗓️", pet.getBestStreak() >= 7, "Alcanza 7 días de racha"));
        badges.add(new BadgeDTO("streak_30", "Mes legendario", "👑", pet.getBestStreak() >= 30, "Alcanza 30 días de racha"));
        badges.add(new BadgeDTO("all_today", "Día completo", "✅", todasHoy, "Completa todas las rutinas del día"));
        badges.add(new BadgeDTO("cared", "Bien cuidado", "🌟", wellnessScore >= 80, "Bienestar sobre 80%"));
        badges.add(new BadgeDTO("vaccinated", "Protegido", "🛡️", vacunasAlDia, "Mantén las vacunas al día"));
        return badges;
    }

    // === Asistente BubblePat: mensajes inteligentes por reglas ===
    private List<InsightDTO> calcularInsights(Pet pet, List<Routine> rutinas, List<Vaccination> vacunas,
                                              List<Reminder> recordatorios, String streakStatus,
                                              int effectiveStreak, boolean doneToday) {
        LocalDate today = LocalDate.now();
        List<InsightDTO> out = new ArrayList<>();

        // 1) Rachas (ánimo / felicitación)
        if (effectiveStreak >= 25) {
            out.add(new InsightDTO("praise", "🔥", "¡Excelente trabajo!",
                    "Llevas " + effectiveStreak + " días consecutivos cuidando a " + pet.getName() + "."));
        } else if (effectiveStreak >= 7) {
            out.add(new InsightDTO("praise", "🎉", "¡Vas genial!",
                    effectiveStreak + " días de racha, " + pet.getName() + " te lo agradece."));
        } else if (effectiveStreak >= 3) {
            out.add(new InsightDTO("praise", "👏", "¡Sigue así!",
                    "Ya son " + effectiveStreak + " días seguidos. ¡No rompas la racha!"));
        }

        // 2) Vacunas por vencer o vencidas
        for (Vaccination v : vacunas) {
            if (v.getNextDoseDate() == null) continue;
            long days = ChronoUnit.DAYS.between(today, v.getNextDoseDate());
            if (days < 0) {
                out.add(new InsightDTO("alert", "💉", "Vacuna vencida",
                        "La vacuna \"" + v.getName() + "\" de " + pet.getName() + " está vencida. Revisa su próxima dosis."));
            } else if (days <= 7) {
                out.add(new InsightDTO("warning", "💉", "Vacuna por vencer",
                        "La vacuna \"" + v.getName() + "\" vence en " + days + " día" + (days == 1 ? "" : "s") + "."));
            }
        }

        // 3) Recordatorios pendientes (vencidos / hoy)
        long pendientes = recordatorios.stream().filter(r -> !r.isCompleted() && r.getReminderDate() != null
                && !r.getReminderDate().toLocalDate().isAfter(today)).count();
        if (pendientes > 0) {
            out.add(new InsightDTO("warning", "🔔", "Recordatorios pendientes",
                    "Tienes " + pendientes + " recordatorio" + (pendientes == 1 ? "" : "s") + " pendiente" + (pendientes == 1 ? "" : "s") + " para " + pet.getName() + "."));
        }

        // 4) Baño: días desde el último baño registrado (solo si la mascota tiene rutina de baño)
        boolean tieneBano = rutinas.stream().anyMatch(r -> "bath".equals(r.getType()));
        if (tieneBano) {
            LocalDate ultimoBano = rutinas.stream()
                    .filter(r -> "bath".equals(r.getType()) && r.isCompleted() && r.getCompletedAt() != null)
                    .map(r -> r.getCompletedAt().toLocalDate())
                    .max(LocalDate::compareTo).orElse(null);
            if (ultimoBano == null) {
                out.add(new InsightDTO("info", "🛁", "Sin baños registrados",
                        "Aún no registras baños para " + pet.getName() + ". ¿Es hora del primero?"));
            } else {
                long dias = ChronoUnit.DAYS.between(ultimoBano, today);
                if (dias >= 10) {
                    out.add(new InsightDTO("alert", "🛁", "Baño pendiente",
                            "Hace " + dias + " días que no registras un baño para " + pet.getName() + "."));
                }
            }
        }

        // 5) Rutinas de hoy
        List<Routine> deHoy = rutinas.stream().filter(r -> aplicaHoy(r, today)).collect(Collectors.toList());
        if (!deHoy.isEmpty()) {
            long hechas = deHoy.stream().filter(r -> r.isCompleted() && r.getCompletedAt() != null
                    && r.getCompletedAt().toLocalDate().equals(today)).count();
            if (hechas == deHoy.size()) {
                out.add(new InsightDTO("praise", "✅", "¡Día completo!",
                        "Hoy cuidaste de " + pet.getName() + ". Completaste todas las rutinas del día."));
            } else {
                long faltan = deHoy.size() - hechas;
                out.add(new InsightDTO("info", "📋", "Rutinas de hoy",
                        "Te faltan " + faltan + " rutina" + (faltan == 1 ? "" : "s") + " para completar el día de " + pet.getName() + "."));
            }
        } else if (rutinas.isEmpty()) {
            out.add(new InsightDTO("info", "✨", "Empieza a cuidar a " + pet.getName(),
                    "Agrega rutinas diarias (alimentación, paseo, agua...) para que " + pet.getName() + " esté siempre al día."));
        }

        // 6) Peso sin registrar
        if (pet.getWeight() == null) {
            out.add(new InsightDTO("info", "⚖️", "Peso sin registrar",
                    "Registra el peso de " + pet.getName() + " para vigilar su salud."));
        }

        // 7) Tip de cuidado (uno por especie, rotando por día para que no sea repetitivo)
        String tip = tipDeCuidado(pet, today);
        if (tip != null) {
            out.add(new InsightDTO("tip", "💡", "Tip de cuidado", tip));
        }

        // === Reglas adicionales ===
        // Medicina pendiente hoy (alta prioridad)
        long medPend = rutinas.stream().filter(r -> "medicine".equals(r.getType())
                && aplicaHoy(r, today)
                && !(r.isCompleted() && r.getCompletedAt() != null && r.getCompletedAt().toLocalDate().equals(today))).count();
        if (medPend > 0) {
            out.add(new InsightDTO("alert", "💊", "Medicina pendiente",
                    "Recuerda dar la medicina de " + pet.getName() + " hoy."));
        }
        // Sin vacunas registradas
        if (vacunas.isEmpty()) {
            out.add(new InsightDTO("info", "💉", "Sin vacunas",
                    "Aún no registras vacunas para " + pet.getName() + ". Considera su plan de vacunación."));
        }
        // Desparasitación (se recomienda cada ~3 meses)
        if (pet.getLastDeworming() == null) {
            out.add(new InsightDTO("info", "🐛", "Desparasitación",
                    "Sin desparasitación registrada para " + pet.getName() + "."));
        } else {
            long meses = ChronoUnit.MONTHS.between(pet.getLastDeworming(), today);
            if (meses >= 3) {
                out.add(new InsightDTO("warning", "🐛", "Desparasitación",
                        "Hace " + meses + " mese" + (meses == 1 ? "" : "s") + " desde la última desparasitación de " + pet.getName() + "."));
            }
        }
        // Alergias conocidas
        if (pet.getAllergicTo() != null && !pet.getAllergicTo().isBlank()) {
            out.add(new InsightDTO("info", "⚠️", "Recuerda su alergia",
                    pet.getName() + " es alérgico/a a " + pet.getAllergicTo() + "."));
        }
        // Racha rota: motivar a recomenzar
        if ("broken".equals(streakStatus)) {
            out.add(new InsightDTO("info", "🔄", "Racha interrumpida",
                    "La racha se rompió. ¡Hoy es un buen día para volver a empezar!"));
        }

        // Orden: alert > warning > praise > info > tip. Máximo 6 mensajes.
        Map<String, Integer> orden = new HashMap<>();
        orden.put("alert", 0);
        orden.put("warning", 1);
        orden.put("praise", 2);
        orden.put("info", 3);
        orden.put("tip", 4);
        out.sort(Comparator.comparingInt(i -> orden.getOrDefault(i.getType(), 9)));
        if (out.size() > 6) out.subList(6, out.size()).clear();
        return out;
    }

    private String tipDeCuidado(Pet pet, LocalDate today) {
        List<String> tips;
        if ("Perro".equals(pet.getSpecies())) {
            tips = List.of(
                    "Un paseo diario mantiene a " + pet.getName() + " feliz y saludable.",
                    "Revisa las patitas y orejas de " + pet.getName() + " cada semana.",
                    "La hidratación es clave: siempre deja agua fresca disponible.");
        } else if ("Gato".equals(pet.getSpecies())) {
            tips = List.of(
                    "Un rascador evita que " + pet.getName() + " dañe los muebles.",
                    "Cepilla a " + pet.getName() + " para reducir las bolas de pelo.",
                    "A los gatos les encanta el juego y las alturas para trepar.");
        } else {
            tips = List.of(
                    "La constancia en el cuidado fortalece el vínculo con " + pet.getName() + ".",
                    "Un ambiente limpio y tranquilo ayuda a " + pet.getName() + " a estar sano.",
                    "Observa cambios en el comportamiento de " + pet.getName() + "; son señales de salud.");
        }
        int idx = (int) ((pet.getId() + today.getDayOfYear()) % tips.size());
        return tips.get(idx);
    }

    private RoutineResponse toRoutineResponse(Routine r) {
        RoutineResponse resp = new RoutineResponse();
        resp.setId(r.getId());
        resp.setType(r.getType());
        resp.setTypeLabel(etiquetaTipoRutina(r.getType()));
        resp.setDescription(r.getDescription());
        resp.setStartTime(r.getStartTime());
        resp.setEndTime(r.getEndTime());
        resp.setDaysOfWeek(r.getDaysOfWeek());
        resp.setAppliesToday(aplicaHoy(r, LocalDate.now()));
        resp.setCompletedAt(r.getCompletedAt());
        resp.setCompleted(r.isCompleted());
        resp.setDoneToday(r.isCompleted() && r.getCompletedAt() != null
                && r.getCompletedAt().toLocalDate().equals(LocalDate.now()));
        return resp;
    }

    private VaccinationResponse toVaccinationResponse(Vaccination v) {
        VaccinationResponse resp = new VaccinationResponse();
        resp.setId(v.getId());
        resp.setName(v.getName());
        resp.setAppliedDate(v.getAppliedDate());
        resp.setNextDoseDate(v.getNextDoseDate());
        resp.setVetName(v.getVetName());
        resp.setNotes(v.getNotes());

        LocalDate today = LocalDate.now();
        if (v.getNextDoseDate() == null) {
            resp.setStatus("sin_proxima");
            resp.setDaysUntilNext(Long.MAX_VALUE);
        } else {
            long days = ChronoUnit.DAYS.between(today, v.getNextDoseDate());
            resp.setDaysUntilNext(days);
            if (days < 0) resp.setStatus("vencida");
            else if (days <= 30) resp.setStatus("por_vencer");
            else resp.setStatus("al_dia");
        }
        return resp;
    }

    private ReminderResponse toReminderResponse(Reminder rem) {
        ReminderResponse resp = new ReminderResponse();
        resp.setId(rem.getId());
        resp.setTitle(rem.getTitle());
        resp.setDescription(rem.getDescription());
        resp.setReminderDate(rem.getReminderDate());
        resp.setCompleted(rem.isCompleted());
        resp.setAutomatic(rem.getSource() != null);

        LocalDateTime now = LocalDateTime.now();
        if (rem.getReminderDate() == null) {
            resp.setStatus("sin_fecha");
            resp.setDaysUntil(Long.MAX_VALUE);
        } else {
            long days = ChronoUnit.DAYS.between(now.toLocalDate(), rem.getReminderDate().toLocalDate());
            resp.setDaysUntil(days);
            if (days < 0) {
                resp.setStatus("vencido");
            } else if (days == 0) {
                resp.setStatus("hoy");
            } else if (days <= 3) {
                resp.setStatus("proximo");
            } else {
                resp.setStatus("futuro");
            }
        }
        return resp;
    }

    private ActivityLogResponse toActivityLogResponse(ActivityLog log) {
        ActivityLogResponse resp = new ActivityLogResponse();
        resp.setId(log.getId());
        resp.setType(log.getType());
        resp.setTitle(log.getTitle());
        resp.setIcon(log.getIcon());
        resp.setCreatedAt(log.getCreatedAt());
        return resp;
    }

    // Etiquetas e iconos humanos para los tipos de rutina (compartido por bienestar y línea de tiempo).
    private String etiquetaTipoRutina(String tipo) {
        if (tipo == null) return "Rutina";
        return switch (tipo) {
            case "feeding" -> "Alimentación";
            case "walk" -> "Paseo";
            case "water" -> "Agua";
            case "medicine" -> "Medicina";
            case "bath" -> "Baño";
            default -> tipo;
        };
    }

    private String iconoTipoRutina(String tipo) {
        if (tipo == null) return "✅";
        return switch (tipo) {
            case "feeding" -> "🍖";
            case "walk" -> "🦮";
            case "water" -> "💧";
            case "medicine" -> "💊";
            case "bath" -> "🛁";
            default -> "✅";
        };
    }
}
