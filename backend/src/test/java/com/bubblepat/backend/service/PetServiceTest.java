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

   }
