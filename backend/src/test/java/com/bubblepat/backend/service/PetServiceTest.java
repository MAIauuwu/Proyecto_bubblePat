package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.*;
import com.bubblepat.backend.model.*;
import com.bubblepat.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private VaccinationRepository vaccinationRepository;
    @Mock
    private ReminderRepository reminderRepository;
    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private PetService petService;

    private User owner;
    private User otro;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("dueno@bubblepat.com");

        otro = new User();
        otro.setId(2L);
        otro.setEmail("intruso@bubblepat.com");
    }

    private Pet petDeOwner() {
        Pet pet = new Pet();
        pet.setId(10L);
        pet.setName("Luna");
        pet.setSpecies("Perro");
        pet.setUser(owner);
        return pet;
    }

    private PetRequest requestValido() {
        PetRequest r = new PetRequest();
        r.setName("Luna");
        r.setSpecies("Perro");
        r.setBreed("Labrador");
        r.setWeight(20.0);
        return r;
    }

    private Routine rutina(String tipo, boolean completada, LocalDate fecha) {
        Routine r = new Routine();
        r.setId(1L);
        r.setType(tipo);
        r.setCompleted(completada);
        if (completada && fecha != null) r.setCompletedAt(fecha.atStartOfDay());
        return r;
    }

    // ===================== OBTENER POR ID =====================

    @Test
    void obtenerPorId_devuelveMascotaDelUsuario() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));

        PetResponse resp = petService.obtenerPorId(10L, "dueno@bubblepat.com");
        assertEquals("Luna", resp.getName());
    }

    @Test
    void obtenerPorId_otroUsuario_lanzaExcepcion() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));

        assertThrows(RuntimeException.class, () -> petService.obtenerPorId(10L, "intruso@bubblepat.com"));
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> petService.obtenerPorId(99L, "dueno@bubblepat.com"));
    }

    // ===================== ACTUALIZAR =====================

    @Test
    void actualizar_modificaCampos() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        PetRequest req = requestValido();
        req.setName("Luna Nueva");

        PetResponse resp = petService.actualizar(10L, req, "dueno@bubblepat.com");
        assertEquals("Luna Nueva", resp.getName());
    }

    @Test
    void actualizar_otroUsuario_lanza() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        assertThrows(RuntimeException.class,
                () -> petService.actualizar(10L, requestValido(), "intruso@bubblepat.com"));
    }
// ===================== ELIMINAR =====================

    @Test
    void eliminar_borraActividadesYMascota() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));

        petService.eliminar(10L, "dueno@bubblepat.com");

        verify(activityLogRepository).deleteByPetId(10L);
        verify(petRepository).delete(pet);
    }

    @Test
    void eliminar_otroUsuario_lanza() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        assertThrows(RuntimeException.class, () -> petService.eliminar(10L, "intruso@bubblepat.com"));
    }
// ===================== RACHA (núcleo del negocio) =====================

    @Test
    void actualizarRacha_primeraVez_streakEn1() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        PetResponse resp = petService.actualizarRacha(10L, "dueno@bubblepat.com");

        assertEquals(1, pet.getDailyStreak());
        assertEquals(1, pet.getBestStreak());
        assertEquals(LocalDate.now(), pet.getLastRoutineDate());
        assertTrue(resp.isRoutineDoneToday());
    }
}