package com.bubblepat.backend.controller;

import com.bubblepat.backend.model.Pet;
import com.bubblepat.backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public List<Pet> getAll() {
        return petService.listarTodas();
    }

    @PostMapping
    public Pet create(@RequestBody Pet pet) {
        return petService.guardarMascota(pet);
    }
}