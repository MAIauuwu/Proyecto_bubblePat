package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.BreedImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DogApiService {

    // The Dog API (no requiere key para bajo volumen). Reemplaza a dog.ceo.
    private static final String BASE_URL = "https://api.thedogapi.com/v1";

    @Autowired
    private RestTemplate restTemplate;

    // Cache ligera de razas (id <-> nombre) para resolver imágenes por raza.
    private volatile List<Map<String, Object>> breedsCache = null;

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getAllBreeds() {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (Map<String, Object> b : fetchBreeds()) {
            Object name = b.get("name");
            if (name != null) out.put(name.toString(), List.of());
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public BreedImageDTO getRandomImageByBreed(String breed) {
        if (breed == null || breed.isBlank()) return getRandomImage();
        try {
            Integer breedId = findBreedId(breed);
            if (breedId != null) {
                List<Map<String, Object>> list = restTemplate.getForObject(
                        BASE_URL + "/images/search?breed_ids=" + breedId + "&limit=1", List.class);
                String url = firstImageUrl(list);
                if (url != null) {
                    return dto(url, breed);
                }
            }
        } catch (Exception ignored) {
        }
        // Si no se encuentra la raza o falla la API, devolvemos un perro aleatorio.
        return getRandomImage();
    }

    @SuppressWarnings("unchecked")
    public BreedImageDTO getRandomImage() {
        try {
            List<Map<String, Object>> list = restTemplate.getForObject(
                    BASE_URL + "/images/search?limit=1", List.class);
            String url = firstImageUrl(list);
            if (url != null) {
                String name = firstBreedName(list);
                return dto(url, name);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String getBreedImageUrl(String breed) {
        try {
            BreedImageDTO dto = getRandomImageByBreed(breed);
            return dto != null ? dto.getImageUrl() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // === Helpers ===

    private BreedImageDTO dto(String url, String breed) {
        BreedImageDTO d = new BreedImageDTO();
        d.setImageUrl(url);
        d.setBreed(breed);
        return d;
    }

    @SuppressWarnings("unchecked")
    private String firstImageUrl(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) return null;
        Object url = list.get(0).get("url");
        return url != null ? url.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private String firstBreedName(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) return "perro";
        Object breeds = list.get(0).get("breeds");
        if (breeds instanceof List<?> bList && !bList.isEmpty()) {
            Object first = ((List<Object>) breeds).get(0);
            if (first instanceof Map<?, ?> bMap) {
                Object name = bMap.get("name");
                if (name != null) return name.toString().toLowerCase();
            }
        }
        return "perro";
    }

    @SuppressWarnings("unchecked")
    private synchronized List<Map<String, Object>> fetchBreeds() {
        if (breedsCache != null) return breedsCache;
        try {
            List<Map<String, Object>> list = restTemplate.getForObject(BASE_URL + "/breeds?limit=200", List.class);
            breedsCache = list != null ? list : Collections.emptyList();
        } catch (Exception e) {
            breedsCache = Collections.emptyList();
        }
        return breedsCache;
    }

    private Integer findBreedId(String breed) {
        String target = breed.toLowerCase().trim();
        for (Map<String, Object> b : fetchBreeds()) {
            Object name = b.get("name");
            if (name != null && name.toString().toLowerCase().equals(target)) {
                Object id = b.get("id");
                if (id instanceof Number) return ((Number) id).intValue();
            }
        }
        // Coincidencia parcial si no hay exacta.
        for (Map<String, Object> b : fetchBreeds()) {
            Object name = b.get("name");
            if (name != null && (name.toString().toLowerCase().contains(target) || target.contains(name.toString().toLowerCase()))) {
                Object id = b.get("id");
                if (id instanceof Number) return ((Number) id).intValue();
            }
        }
        return null;
    }
}
