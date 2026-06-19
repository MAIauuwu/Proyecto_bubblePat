package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.BreedImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DogApiService {

    private static final String BASE_URL = "https://dog.ceo/api";

    private static final Map<String, String> BREED_SLUGS = buildSlugMap();

    private static Map<String, String> buildSlugMap() {
        Map<String, String> m = new HashMap<>();
        m.put("golden retriever", "retriever/golden");
        m.put("labrador retriever", "retriever/labrador");
        m.put("german shepherd", "germanshepherd");
        m.put("french bulldog", "bulldog/french");
        m.put("english bulldog", "bulldog/english");
        m.put("boston bulldog", "bulldog/boston");
        m.put("border collie", "collie/border");
        m.put("australian shepherd", "australian/shepherd");
        m.put("siberian husky", "husky");
        m.put("great dane", "dane/great");
        m.put("bernese mountain", "mountain/bernese");
        m.put("swiss mountain", "mountain/swiss");
        m.put("scottish deerhound", "deerhound/scottish");
        m.put("norwegian buhund", "buhund/norwegian");
        m.put("norwegian elkhound", "elkhound/norwegian");
        m.put("italian greyhound", "greyhound/italian");
        m.put("cocker spaniel", "spaniel/cocker");
        m.put("irish setter", "setter/irish");
        m.put("english setter", "setter/english");
        m.put("gordon setter", "setter/gordon");
        m.put("miniature pinscher", "pinscher/miniature");
        m.put("miniature schnauzer", "schnauzer/miniature");
        m.put("giant schnauzer", "schnauzer/giant");
        m.put("shetland sheepdog", "sheepdog/shetland");
        m.put("english sheepdog", "sheepdog/english");
        m.put("yorkshire terrier", "terrier/yorkshire");
        m.put("staffordshire bullterrier", "bullterrier/staffordshire");
        m.put("rhodesian ridgeback", "ridgeback/rhodesian");
        m.put("caucasian ovcharka", "ovcharka/caucasian");
        m.put("italian segugio", "segugio/italian");
        m.put("japanese spitz", "spitz/japanese");
        m.put("spanish waterdog", "waterdog/spanish");
        m.put("irish wolfhound", "wolfhound/irish");
        m.put("afghan hound", "hound/afghan");
        m.put("basset hound", "hound/basset");
        m.put("blood hound", "hound/blood");
        m.put("english hound", "hound/english");
        m.put("ibizan hound", "hound/ibizan");
        m.put("plott hound", "hound/plott");
        m.put("walker hound", "hound/walker");
        return m;
    }

    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getAllBreeds() {
        Map<String, Object> response = restTemplate.getForObject(
                BASE_URL + "/breeds/list/all", Map.class);
        if (response != null && "success".equals(response.get("status"))) {
            return (Map<String, List<String>>) response.get("message");
        }
        return Map.of();
    }

    public BreedImageDTO getRandomImageByBreed(String breed) {
        String slug = toSlug(breed);
        Map<String, Object> response = restTemplate.getForObject(
                BASE_URL + "/breed/" + slug + "/images/random", Map.class);
        if (response != null && "success".equals(response.get("status"))) {
            BreedImageDTO dto = new BreedImageDTO();
            dto.setImageUrl((String) response.get("message"));
            dto.setBreed(breed);
            return dto;
        }
        return null;
    }

    public BreedImageDTO getRandomImage() {
        Map<String, Object> response = restTemplate.getForObject(
                BASE_URL + "/breeds/image/random", Map.class);
        if (response != null && "success".equals(response.get("status"))) {
            BreedImageDTO dto = new BreedImageDTO();
            dto.setImageUrl((String) response.get("message"));
            String url = (String) response.get("message");
            String[] parts = url.split("/");
            if (parts.length > 4) {
                dto.setBreed(parts[4].replace("-", " "));
            }
            return dto;
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

    private String toSlug(String breed) {
        if (breed == null) return "";
        String lower = breed.toLowerCase().trim();
        return BREED_SLUGS.getOrDefault(lower, lower.replace(" ", "-"));
    }
}
