package com.bubblepat.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

// Obtiene imágenes reales de especies sin API dedicada (Ave, Conejo, Pez, Otro)
// desde Wikimedia Commons. Sin clave de API, confiable y con cache en memoria.
@Service
public class GenericImageService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageCache imageCache;

    private static final String API_URL = "https://commons.wikimedia.org/w/api.php";

    // Termino de busqueda por especie (en ingles para mejores resultados).
    private static final Map<String, String> SEARCH_TERMS = Map.of(
            "Conejo", "domestic rabbit",
            "Ave", "pet parakeet",
            "Pez", "goldfish aquarium",
            "Otro", "cute pet"
    );

    // Cache en memoria: especie -> lista de URLs (se llena la primera vez).
    private final Map<String, List<String>> cache = new HashMap<>();

    public String getImage(String species, Integer seed) {
        if (species == null) return null;
        String search = SEARCH_TERMS.get(species);
        if (search == null) return null;

        List<String> urls = cache.computeIfAbsent(species, k -> fetchUrls(search));
        if (urls.isEmpty()) return null;

        int idx = seed != null ? Math.abs(seed) % urls.size() : new Random().nextInt(urls.size());
        return urls.get(idx);
    }

    @SuppressWarnings("unchecked")
    private List<String> fetchUrls(String search) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("action", "query")
                    .queryParam("generator", "search")
                    .queryParam("gsrsearch", search)
                    .queryParam("gsrnamespace", 6)
                    .queryParam("gsrlimit", 15)
                    .queryParam("prop", "imageinfo")
                    .queryParam("iiprop", "url")
                    .queryParam("iiurlwidth", 600)
                    .queryParam("format", "json")
                    .queryParam("origin", "*")
                    .build()
                    .toUri();

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> body = response.getBody();
            if (body == null) return List.of();

            Map<String, Object> query = (Map<String, Object>) body.get("query");
            if (query == null) return List.of();

            Map<String, Object> pages = (Map<String, Object>) query.get("pages");
            if (pages == null) return List.of();

            List<String> urls = new ArrayList<>();
            for (Object value : pages.values()) {
                Map<String, Object> page = (Map<String, Object>) value;
                List<Map<String, Object>> info = (List<Map<String, Object>>) page.get("imageinfo");
                if (info != null && !info.isEmpty()) {
                    Object url = info.get(0).get("thumburl");
                    if (url != null) urls.add(url.toString());
                }
            }
            return urls;
        } catch (Exception e) {
            return List.of();
        }
    }
}
