package com.bubblepat.backend.controller;

import com.bubblepat.backend.service.BreedSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Proxy de búsqueda de razas. El frontend llama a estos endpoints en lugar de
// consultar API Ninjas directamente, manteniendo la clave de API en el backend.
@RestController
@RequestMapping("/api/breeds")
@CrossOrigin(origins = "*")
public class BreedSearchController {

    @Autowired
    private BreedSearchService breedSearchService;

    @GetMapping("/dogs")
    public ResponseEntity<List<String>> searchDogs(@RequestParam("q") String q) {
        return ResponseEntity.ok(breedSearchService.searchDogs(q));
    }

    @GetMapping("/cats")
    public ResponseEntity<List<String>> searchCats(@RequestParam("q") String q) {
        return ResponseEntity.ok(breedSearchService.searchCats(q));
    }
}
