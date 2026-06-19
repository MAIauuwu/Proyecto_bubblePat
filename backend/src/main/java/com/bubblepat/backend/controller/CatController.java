package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.CatBreedDTO;
import com.bubblepat.backend.dto.CatImageDTO;
import com.bubblepat.backend.service.CatApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
@CrossOrigin(origins = "*")
public class CatController {

    @Autowired
    private CatApiService catApiService;

    @GetMapping("/breeds")
    public List<CatBreedDTO> getAllBreeds() {
        return catApiService.getAllBreeds();
    }

    @GetMapping("/random")
    public CatImageDTO getRandomImage() {
        return catApiService.getRandomImage();
    }

    @GetMapping("/image")
    public CatImageDTO getImageByBreed(@RequestParam String breedId) {
        return catApiService.getImageByBreed(breedId);
    }

    @GetMapping("/breed")
    public CatBreedDTO getBreedById(@RequestParam String breedId) {
        return catApiService.getBreedById(breedId);
    }
}
