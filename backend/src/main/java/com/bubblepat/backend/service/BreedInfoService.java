package com.bubblepat.backend.service;

import com.bubblepat.backend.dto.BreedInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BreedInfoService {

    private static final String BASE_URL = "https://api.api-ninjas.com/v1/dogs";

    @Value("${api.ninjas.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public BreedInfoDTO getBreedInfo(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<BreedInfoDTO>> response = restTemplate.exchange(
                BASE_URL + "?name=" + name,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            return response.getBody().get(0);
        }
        return null;
    }
}
