package com.bubblepat.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

@SpringBootApplication
public class Main {

    // Carga un archivo .env (en el directorio de trabajo) y expone sus variables
    // como propiedades del sistema, de modo que los placeholders ${VAR} de
    // application.properties se resuelvan. Así evitamos commitear credenciales
    // reales al repositorio. Las variables de entorno reales tienen prioridad.
    static {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (Stream<String> lines = Files.lines(envFile.toPath())) {
                lines.forEach(line -> {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) return;
                    int eq = trimmed.indexOf('=');
                    if (eq < 0) return;
                    String key = trimmed.substring(0, eq).trim();
                    String value = trimmed.substring(eq + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\""))
                            || (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (System.getenv(key) == null && System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                });
            } catch (Exception ignored) {
                // Si la lectura falla, se usan las variables de entorno reales.
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}