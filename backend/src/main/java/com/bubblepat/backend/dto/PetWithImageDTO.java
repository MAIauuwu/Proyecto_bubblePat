package com.bubblepat.backend.dto;

import com.bubblepat.backend.model.Pet;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PetWithImageDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private Double weight;
    private String allergicTo;
    private LocalDate lastDeworming;
    private int dailyStreak;
    private LocalDate lastRoutineDate;
    private String breedImageUrl;

    public static PetWithImageDTO from(Pet pet, String breedImageUrl) {
        PetWithImageDTO dto = new PetWithImageDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setBirthDate(pet.getBirthDate());
        dto.setWeight(pet.getWeight());
        dto.setAllergicTo(pet.getAllergicTo());
        dto.setLastDeworming(pet.getLastDeworming());
        dto.setDailyStreak(pet.getDailyStreak());
        dto.setLastRoutineDate(pet.getLastRoutineDate());
        dto.setBreedImageUrl(breedImageUrl);
        return dto;
    }
}
