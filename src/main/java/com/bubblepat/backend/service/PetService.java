package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.*;
import com.bubblepat.backend.model.Pet;
import com.bubblepat.backend.model.Routine;
import com.bubblepat.backend.model.Vaccination;
import com.bubblepat.backend.model.Reminder;
import com.bubblepat.backend.model.User;
import com.bubblepat.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final VaccinationRepository vaccinationRepository;
    private final ReminderRepository reminderRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository,
                      RoutineRepository routineRepository, VaccinationRepository vaccinationRepository,
                      ReminderRepository reminderRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.reminderRepository = reminderRepository;
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

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setBirthDate(request.getBirthDate());
        pet.setWeight(request.getWeight());
        pet.setAllergicTo(request.getAllergicTo());
        pet.setLastDeworming(request.getLastDeworming());
        pet.setUser(user);

        return toResponse(petRepository.save(pet));
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

        return toResponse(petRepository.save(pet));
    }

    public void eliminar(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso");
        }
        petRepository.delete(pet);
    }

    public PetResponse actualizarRacha(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso");
        }

        LocalDate today = LocalDate.now();
        if (pet.getLastRoutineDate() != null && pet.getLastRoutineDate().equals(today)) {
            throw new RuntimeException("Ya completaste la rutina de hoy");
        }

        if (pet.getLastRoutineDate() != null && pet.getLastRoutineDate().equals(today.minusDays(1))) {
            pet.setDailyStreak(pet.getDailyStreak() + 1);
        } else if (pet.getLastRoutineDate() == null) {
            pet.setDailyStreak(1);
        } else {
            pet.setDailyStreak(1);
        }
        pet.setLastRoutineDate(today);

        return toResponse(petRepository.save(pet));
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
        routine = routineRepository.save(routine);
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
        routine.setCompleted(true);
        routine.setCompletedAt(java.time.LocalDateTime.now());
        return toRoutineResponse(routineRepository.save(routine));
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
        return toVaccinationResponse(vaccinationRepository.save(vac));
    }

    public List<VaccinationResponse> listarVacunas(Long petId, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        if (!pet.getUser().getEmail().equals(email)) throw new RuntimeException("No tienes permiso");
        return vaccinationRepository.findByPetId(petId).stream().map(this::toVaccinationResponse).collect(Collectors.toList());
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
        r.setDailyStreak(pet.getDailyStreak());
        r.setLastRoutineDate(pet.getLastRoutineDate());
        r.setRoutines(routineRepository.findByPetId(pet.getId()).stream().map(this::toRoutineResponse).collect(Collectors.toList()));
        r.setVaccinations(vaccinationRepository.findByPetId(pet.getId()).stream().map(this::toVaccinationResponse).collect(Collectors.toList()));
        r.setReminders(reminderRepository.findByPetId(pet.getId()).stream().map(this::toReminderResponse).collect(Collectors.toList()));
        return r;
    }

    private RoutineResponse toRoutineResponse(Routine r) {
        RoutineResponse resp = new RoutineResponse();
        resp.setId(r.getId());
        resp.setType(r.getType());
        resp.setDescription(r.getDescription());
        resp.setCompletedAt(r.getCompletedAt());
        resp.setCompleted(r.isCompleted());
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
        return resp;
    }

    private ReminderResponse toReminderResponse(Reminder rem) {
        ReminderResponse resp = new ReminderResponse();
        resp.setId(rem.getId());
        resp.setTitle(rem.getTitle());
        resp.setDescription(rem.getDescription());
        resp.setReminderDate(rem.getReminderDate());
        resp.setCompleted(rem.isCompleted());
        return resp;
    }
}