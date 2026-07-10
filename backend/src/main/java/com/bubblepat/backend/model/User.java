package com.bubblepat.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // Tema de color elegido por el usuario (hue HSL 0-360). null = por defecto.
    private Integer themeHue;

    // Plan de suscripción: FREE, PREMIUM_MONTHLY, PREMIUM_ANNUAL, FAMILY
    private String plan = "FREE";

    private LocalDate createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}