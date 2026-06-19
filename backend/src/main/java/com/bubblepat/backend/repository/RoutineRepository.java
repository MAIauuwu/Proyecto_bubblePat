package com.bubblepat.backend.repository;

import com.bubblepat.backend.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByPetId(Long petId);
}
