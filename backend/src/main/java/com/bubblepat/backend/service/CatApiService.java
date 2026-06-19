package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.CatBreedDTO;
import com.bubblepat.backend.dto.CatImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CatApiService {

    private static final String BASE_URL = "https://api.thecatapi.com/v1";

    @Autowired
    private RestTemplate restTemplate;

    public List<CatBreedDTO> getAllBreeds() {
        return restTemplate.exchange(
                BASE_URL + "/breeds",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CatBreedDTO>>() {}
        ).getBody();
    }

    public CatImageDTO getRandomImage() {
        List<CatImageDTO> images = restTemplate.exchange(
                BASE_URL + "/images/search",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CatImageDTO>>() {}
        ).getBody();
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public CatImageDTO getImageByBreed(String breedId) {
        List<CatImageDTO> images = restTemplate.exchange(
                BASE_URL + "/images/search?breed_ids=" + breedId,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CatImageDTO>>() {}
        ).getBody();
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public String getImageUrlByBreedName(String breedName) {
        try {
            List<CatBreedDTO> breeds = getAllBreeds();
            if (breeds != null) {
                String lowerName = breedName.toLowerCase().trim();
                return breeds.stream()
                        .filter(b -> b.getName().toLowerCase().contains(lowerName)
                                || lowerName.contains(b.getName().toLowerCase()))
                        .findFirst()
                        .map(b -> {
                            CatImageDTO img = getImageByBreed(b.getId());
                            return img != null ? img.getUrl() : null;
                        })
                        .orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public CatBreedDTO getBreedById(String breedId) {
        List<CatBreedDTO> breeds = getAllBreeds();
        if (breeds != null) {
            return breeds.stream()
                    .filter(b -> b.getId().equalsIgnoreCase(breedId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
