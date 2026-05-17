package com.bubblepat.backend.service;

import com.bubblepat.backend.model.Pet;
import com.bubblepat.backend.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PetService {


    @Autowired
    private PetRepository petRepository;

    public List<Pet> listarTodas() {
        return petRepository.findAll();
    }

    public Pet guardarMascota(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet obtenerPorId(Long id) {
        return petRepository.findById(id).orElse(null);
    }

    public void eliminarMascota(Long id) {
        petRepository.deleteById(id);
    }

    public Pet actualizarMascota(Long id, Pet petDetails) {
        Pet pet = petRepository.findById(id).orElse(null);
        if (pet != null) {
            pet.setName(petDetails.getName());
            pet.setSpecies(petDetails.getSpecies());
            pet.setBreed(petDetails.getBreed());
            pet.setBirthDate(petDetails.getBirthDate());
            pet.setWeight(petDetails.getWeight());
            pet.setAllergicTo(petDetails.getAllergicTo());
            pet.setLastDeworming(petDetails.getLastDeworming());
            return petRepository.save(pet);
        }
        return null;
    }

    public Pet actualizarRacha(Long id, Pet petDetails) {
        Pet pet = petRepository.findById(id).orElse(null);
        if (pet != null) {
            pet.setDailyStreak(pet.getDailyStreak() + 1);
            pet.setLastRoutineDate(LocalDate.now());
            return petRepository.save(pet);
        }
        return null;
    }
}
