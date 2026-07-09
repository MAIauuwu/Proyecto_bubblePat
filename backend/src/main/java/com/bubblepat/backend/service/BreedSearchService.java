package com.bubblepat.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// Busca razas en API Ninjas desde el backend, de modo que la clave de API
// nunca se exponga en el navegador.
@Service
public class BreedSearchService {

    private static final String BASE_URL = "https://api.api-ninjas.com/v1";

    @Value("${api.ninjas.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> searchDogs(String name) {
        return search("/dogs", name);
    }

    public List<String> searchCats(String name) {
        return search("/cats", name);
    }

    // Devuelve TODAS las razas de una vez (con cache en memoria).
    // Permite que el frontend filtre localmente sin llamar a la API por cada tecla.
    private volatile List<String> dogBreedsCache = null;
    private volatile List<String> catBreedsCache = null;

    public List<String> allDogs() {
        if (dogBreedsCache == null) dogBreedsCache = fetchAllBreeds("/dogs");
        return dogBreedsCache;
    }

    public List<String> allCats() {
        if (catBreedsCache == null) catBreedsCache = fetchAllBreeds("/cats");
        return catBreedsCache;
    }

    @SuppressWarnings("unchecked")
    private List<String> search(String path, String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + path)
                    .queryParam("name", name)
                    .build()
                    .toUri();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() == null) {
                return List.of();
            }
            return response.getBody().stream()
                    .map(m -> (String) m.get("name"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> fetchAllBreeds(String path) {
        List<String> all = new ArrayList<>();
        int offset = 0;
        while (offset < 1000) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Api-Key", apiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + path)
                        .queryParam("limit", 30)
                        .queryParam("offset", offset)
                        .build()
                        .toUri();

                ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                        uri, HttpMethod.GET, entity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {});

                List<Map<String, Object>> body = response.getBody();
                if (body == null || body.isEmpty()) break;

                body.stream()
                        .map(m -> (String) m.get("name"))
                        .filter(Objects::nonNull)
                        .forEach(all::add);

                if (body.size() < 30) break;
                offset += 30;
            } catch (Exception e) {
                break;
            }
        }
        return all;
    }
}
