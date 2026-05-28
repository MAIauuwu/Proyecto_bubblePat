package com.bubblepat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "routines")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @JsonIgnore
    private Pet pet;

    @Column(nullable = false)
    private String type; // "sentir", "comer", "jugar", "pasear", "dormir"

    private String description;
    private LocalDateTime completedAt;
    private boolean completed = false;
}
