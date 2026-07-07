package com.bubblepat.backend.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Caché en memoria para URLs de imágenes de animales (perros, gatos, etc.).
 * Evita llamar a las APIs externas en cada solicitud: la primera petición
 * consulta la API y la guarda; las siguientes (dentro del TTL) se responden
 * al instante desde memoria. Acelera mucho la carga del panel y detalles.
 */
@Component
public class ImageCache {

    private static final long TTL_MS = 6 * 60 * 60 * 1000L; // 6 horas

    private static final class Entry {
        final String value;
        final long expiresAt;
        Entry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Entry> cache = new ConcurrentHashMap<>();

    public String get(String key, Supplier<String> loader) {
        Entry e = cache.get(key);
        long now = System.currentTimeMillis();
        if (e != null && e.expiresAt > now) {
            return e.value;
        }
        String value = loader.get(); // puede ser null si la API falla
        if (value != null) {
            cache.put(key, new Entry(value, now + TTL_MS));
        }
        return value;
    }
}
