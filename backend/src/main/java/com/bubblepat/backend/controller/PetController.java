package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.*;
import com.bubblepat.backend.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getAll(@RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.listarPorUsuario(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getById(@PathVariable Long id, @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.obtenerPorId(id, email));
    }

    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request,
                                              @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.crear(request, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody PetRequest request,
                                              @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.actualizar(id, request, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestAttribute("email") String email) {
        petService.eliminar(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/streak")
    public ResponseEntity<PetResponse> updateStreak(@PathVariable Long id,
                                                    @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.actualizarRacha(id, email));
    }

    // === RUTINAS ===
    @PostMapping("/{petId}/routines")
    public ResponseEntity<RoutineResponse> addRoutine(@PathVariable Long petId,
                                                      @Valid @RequestBody RoutineRequest request,
                                                      @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.agregarRutina(petId, request, email));
    }

    @GetMapping("/{petId}/routines")
    public ResponseEntity<List<RoutineResponse>> getRoutines(@PathVariable Long petId,
                                                             @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.listarRutinas(petId, email));
    }

    @PatchMapping("/routines/{routineId}/complete")
    public ResponseEntity<RoutineResponse> completeRoutine(@PathVariable Long routineId,
                                                           @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.completarRutina(routineId, email));
    }

    @PutMapping("/routines/{routineId}")
    public ResponseEntity<RoutineResponse> updateRoutine(@PathVariable Long routineId,
                                                         @Valid @RequestBody RoutineRequest request,
                                                         @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.editarRutina(routineId, request, email));
    }

    @DeleteMapping("/routines/{routineId}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long routineId,
                                              @RequestAttribute("email") String email) {
        petService.eliminarRutina(routineId, email);
        return ResponseEntity.noContent().build();
    }

    // === VACUNAS ===
    @PostMapping("/{petId}/vaccinations")
    public ResponseEntity<VaccinationResponse> addVaccination(@PathVariable Long petId,
                                                              @Valid @RequestBody VaccinationRequest request,
                                                              @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.agregarVacuna(petId, request, email));
    }

    @GetMapping("/{petId}/vaccinations")
    public ResponseEntity<List<VaccinationResponse>> getVaccinations(@PathVariable Long petId,
                                                                     @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.listarVacunas(petId, email));
    }

    @PutMapping("/vaccinations/{vaccinationId}")
    public ResponseEntity<VaccinationResponse> updateVaccination(@PathVariable Long vaccinationId,
                                                                 @Valid @RequestBody VaccinationRequest request,
                                                                 @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.editarVacuna(vaccinationId, request, email));
    }

    @DeleteMapping("/vaccinations/{vaccinationId}")
    public ResponseEntity<Void> deleteVaccination(@PathVariable Long vaccinationId,
                                                  @RequestAttribute("email") String email) {
        petService.eliminarVacuna(vaccinationId, email);
        return ResponseEntity.noContent().build();
    }

    // === RECORDATORIOS ===
    @PostMapping("/{petId}/reminders")
    public ResponseEntity<ReminderResponse> addReminder(@PathVariable Long petId,
                                                        @Valid @RequestBody ReminderRequest request,
                                                        @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.agregarRecordatorio(petId, request, email));
    }

    @GetMapping("/{petId}/reminders")
    public ResponseEntity<List<ReminderResponse>> getReminders(@PathVariable Long petId,
                                                               @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.listarRecordatorios(petId, email));
    }

    @PatchMapping("/reminders/{reminderId}/complete")
    public ResponseEntity<ReminderResponse> completeReminder(@PathVariable Long reminderId,
                                                              @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.completarRecordatorio(reminderId, email));
    }

    @PutMapping("/reminders/{reminderId}")
    public ResponseEntity<ReminderResponse> updateReminder(@PathVariable Long reminderId,
                                                            @Valid @RequestBody ReminderRequest request,
                                                            @RequestAttribute("email") String email) {
        return ResponseEntity.ok(petService.editarRecordatorio(reminderId, request, email));
    }

    @DeleteMapping("/reminders/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long reminderId,
                                               @RequestAttribute("email") String email) {
        petService.eliminarRecordatorio(reminderId, email);
        return ResponseEntity.noContent().build();
    }
}