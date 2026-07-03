package com.bubblepat.backend.repository;

import com.bubblepat.backend.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop30ByPetIdOrderByCreatedAtDesc(Long petId);

    void deleteByPetId(Long petId);
}
