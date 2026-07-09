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

    @Test
    void actualizarRacha_diaConsecutivo_incrementaStreak() {
        Pet pet = petDeOwner();
        pet.setDailyStreak(5);
        pet.setBestStreak(5);
        pet.setLastRoutineDate(LocalDate.now().minusDays(1));

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        petService.actualizarRacha(10L, "dueno@bubblepat.com");

        assertEquals(6, pet.getDailyStreak());
        assertEquals(6, pet.getBestStreak());
    }

    @Test
    void actualizarRacha_rachaRota_reseteaA1() {
        Pet pet = petDeOwner();
        pet.setDailyStreak(10);
        pet.setBestStreak(10);
        pet.setLastRoutineDate(LocalDate.now().minusDays(3));

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        petService.actualizarRacha(10L, "dueno@bubblepat.com");

        assertEquals(1, pet.getDailyStreak());
        assertEquals(10, pet.getBestStreak()); // el récord no baja
        assertEquals(LocalDate.now(), pet.getLastRoutineDate());
    }

    @Test
    void actualizarRacha_mismoDia_lanzaExcepcion() {
        Pet pet = petDeOwner();
        pet.setLastRoutineDate(LocalDate.now());
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.actualizarRacha(10L, "dueno@bubblepat.com"));
        assertEquals("Ya completaste la rutina de hoy", ex.getMessage());
    }

    @Test
    void actualizarRacha_otroUsuario_lanza() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        assertThrows(RuntimeException.class, () -> petService.actualizarRacha(10L, "intruso@bubblepat.com"));
    }

    @Test
    void actualizarRacha_alcanzaHitoDe3_registraActividadStreak() {
        Pet pet = petDeOwner();
        pet.setDailyStreak(2);
        pet.setBestStreak(2);
        pet.setLastRoutineDate(LocalDate.now().minusDays(1));

        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));

        petService.actualizarRacha(10L, "dueno@bubblepat.com");

        assertEquals(3, pet.getDailyStreak());
        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(activityLogRepository).save(captor.capture());
        assertEquals("STREAK", captor.getValue().getType());
        assertTrue(captor.getValue().getTitle().contains("3"));
    }

    // ===================== RUTINAS =====================

    @Test
    void agregarRutina_guardaRutina() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(routineRepository.save(any(Routine.class))).thenAnswer(i -> i.getArgument(0));
        when(routineRepository.findByPetId(10L)).thenReturn(List.of());

        RoutineRequest req = new RoutineRequest();
        req.setType("feeding");
        req.setDescription("Desayuno");

        RoutineResponse resp = petService.agregarRutina(10L, req, "dueno@bubblepat.com");
        assertEquals("feeding", resp.getType());
        assertEquals("Alimentación", resp.getTypeLabel());
    }

    @Test
    void listarRutinas_devuelveLista() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(routineRepository.findByPetId(10L)).thenReturn(List.of(rutina("walk", false, null)));

        List<RoutineResponse> lista = petService.listarRutinas(10L, "dueno@bubblepat.com");
        assertEquals(1, lista.size());
    }

    @Test
    void completarRutina_marcaCompletadaYSincronizaRacha() {
        Pet pet = petDeOwner();
        Routine r = rutina("feeding", false, null);
        r.setPet(pet);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(r));
        when(routineRepository.save(any(Routine.class))).thenAnswer(i -> i.getArgument(0));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> i.getArgument(0));
        when(routineRepository.findByPetId(10L)).thenReturn(List.of(r));

        RoutineResponse resp = petService.completarRutina(1L, "dueno@bubblepat.com");

        assertTrue(resp.isCompleted());
        assertNotNull(resp.getCompletedAt());
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void editarRutina_actualizaCampos() {
        Pet pet = petDeOwner();
        Routine r = rutina("feeding", false, null);
        r.setPet(pet);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(r));
        when(routineRepository.save(any(Routine.class))).thenAnswer(i -> i.getArgument(0));

        RoutineRequest req = new RoutineRequest();
        req.setType("walk");
        req.setDescription("Paseo");

        RoutineResponse resp = petService.editarRutina(1L, req, "dueno@bubblepat.com");
        assertEquals("walk", resp.getType());
    }

    @Test
    void eliminarRutina_borraYSincroniza() {
        Pet pet = petDeOwner();
        Routine r = rutina("feeding", false, null);
        r.setPet(pet);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(r));
        when(routineRepository.findByPetId(10L)).thenReturn(List.of());

        petService.eliminarRutina(1L, "dueno@bubblepat.com");
        verify(routineRepository).delete(r);
    }

    @Test
    void agregarRutina_otroUsuario_lanza() {
        Pet pet = petDeOwner();
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));

        assertThrows(RuntimeException.class,
                () -> petService.agregarRutina(10L, new RoutineRequest(), "intruso@bubblepat.com"));
    }
}