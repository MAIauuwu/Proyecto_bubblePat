package com.bubblepat.backend.controller;

import com.bubblepat.backend.dto.BreedImageDTO;
import com.bubblepat.backend.dto.BreedInfoDTO;
import com.bubblepat.backend.service.BreedInfoService;
import com.bubblepat.backend.service.DogApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dogs")
@CrossOrigin(origins = "*")
public class DogController {

    @Autowired
    private DogApiService dogApiService;

    @Autowired
    private BreedInfoService breedInfoService;

    @GetMapping("/breeds")
    public Map<String, List<String>> getAllBreeds() {
        return dogApiService.getAllBreeds();
    }

    @GetMapping("/image")
    public BreedImageDTO getImageByBreed(@RequestParam String breed) {
        return dogApiService.getRandomImageByBreed(breed);
    }

    @GetMapping("/random")
    public BreedImageDTO getRandomImage() {
        return dogApiService.getRandomImage();
    }

    @GetMapping("/info")
    public BreedInfoDTO getBreedInfo(@RequestParam String name) {
        return breedInfoService.getBreedInfo(name);
    }
}
