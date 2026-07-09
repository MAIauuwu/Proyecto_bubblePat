package com.bubblepat.backend.controller;

import com.bubblepat.backend.service.GenericImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@CrossOrigin(origins = "*")
public class GenericImageController {

    @Autowired
    private GenericImageService genericImageService;

    @GetMapping("/image")
    public ResponseEntity<Map<String, String>> getImage(
            @RequestParam("species") String species,
            @RequestParam(value = "seed", required = false) Integer seed) {
        String url = genericImageService.getImage(species, seed);
        if (url != null) {
            return ResponseEntity.ok(Map.of("imageUrl", url));
        }
        return ResponseEntity.ok(Map.of("imageUrl", ""));
    }
}
