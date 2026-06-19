# BubblePat — Backend

App de gestión de cuidado de mascotas: fichas médicas, rutinas diarias con sistema de **rachas**, recordatorios y vacunas. Genera imágenes e información de razas integrando APIs externas (dog.ceo, The Cat API, API Ninjas).

> El proyecto completo está compuesto por este backend y un frontend en React. Para una visión integral consulta también el repositorio del frontend (`bubblepat_frontend`).

## Descripción

BubblePat ayuda a quienes tienen mascotas a llevar un registro del cuidado diario: alimentación, paseos, medicinas y baños, premiando la constancia con una **racha de días consecutivos** (y su récord). Además permite agendar **recordatorios** (citas veterinarias, baños, vacunas) con estados de urgencia (vencido / hoy / próximo) y llevar la **ficha médica** con vacunas y datos clínicos.

## Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3.5** (Spring Web, Spring Data JPA, Spring Security, Validation)
- **PostgreSQL 18**
- **JWT (jjwt)** para autenticación
- **Lombok**
- Cargador ligero de `.env` propio (lee variables de entorno sin dependencias externas)
- APIs externas: dog.ceo, The Cat API, API Ninjas

## Requisitos previos

- JDK 17
- Maven 3.9+
- PostgreSQL 18 (base de datos `bubblepat_db`)
- Una clave de [API Ninjas](https://api-ninjas.com) para el buscador de razas

## Instalación

```bash
# Clonar el repositorio
git clone https://github.com/MAIauuwu/Proyecto_bubblePat.git
cd Proyecto_bubblePat

# Compilar (descarga dependencias)
mvn clean compile
```

## Configuración

Las credenciales **no** se commitean. Se leen desde variables de entorno o un archivo `.env` en la raíz del proyecto.

1. Copia la plantilla y completa tus valores:

```bash
cp .env.example .env
```

2. Edita `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/bubblepat_db
DB_USER=postgres
DB_PASSWORD=tu_password_de_postgres
API_NINJAS_KEY=tu_api_ninjas_key
JWT_SECRET=cadena-secreta-larga-y-aleatoria-para-jwt
```

Estos valores se inyectan en `src/main/resources/application.properties` mediante placeholders `${VAR}`.

## Uso / Ejecución

```bash
# Crear la base de datos (una sola vez)
createdb -U postgres bubblepat_db

# Levantar el backend (puerto 8081)
mvn spring-boot:run
```

El servidor queda en `http://localhost:8081`. El esquema de tablas se crea/actualiza automáticamente (`spring.jpa.hibernate.ddl-auto=update`).

## Arquitectura del proyecto

Arquitectura en capas clásica de Spring Boot:

```
src/main/java/com/bubblepat/backend/
├── Main.java                 # Arranque + carga de .env
├── config/                   # Configuración (RestTemplate)
├── controller/               # Controladores REST (auth, pets, dogs, cats, breeds)
├── dto/                      # Objetos de transferencia (request/response)
├── exception/                # Manejo global de errores
├── model/                    # Entidades JPA (User, Pet, Routine, Vaccination, Reminder)
├── repository/               # Repositorios Spring Data JPA
├── security/                 # JWT, filtros y configuración de Security
└── service/                  # Lógica de negocio (PetService, AuthService, APIs)
```

- **Seguridad:** JWT sin estado (stateless). Los endpoints `/api/auth/**`, `/api/dogs/**`, `/api/cats/**` y `/api/breeds/**` son públicos; el resto requiere token.
- **Negocio central:** `PetService` gestiona mascotas, rachas, rutinas, vacunas y recordatorios.

## Base de datos

PostgreSQL. Tablas principales (mapeo JPA, generadas automáticamente):

- `users` — usuarios de la app (autenticación).
- `pets` — mascotas, con datos clínicos y rachas (`daily_streak`, `best_streak`, `last_routine_date`).
- `routines` — rutinas diarias por mascota (tipo, descripción, estado).
- `reminders` — recordatorios por mascota (título, fecha, estado de completado).
- `vaccinations` — vacunas aplicadas y próximas dosis.

Relaciones: un `User` (1) → (N) `Pet`; un `Pet` (1) → (N) `Routine` / `Reminder` / `Vaccination`.

## Documentación de la API

Base: `http://localhost:8081/api`

### Autenticación (`/auth`) — público
| Método | Ruta            | Descripción                  |
|--------|-----------------|------------------------------|
| POST   | `/auth/register`| Registro de usuario          |
| POST   | `/auth/login`   | Login, devuelve JWT          |

### Mascotas (`/pets`) — requiere JWT
| Método | Ruta                       | Descripción                                   |
|--------|----------------------------|-----------------------------------------------|
| GET    | `/pets`                    | Lista las mascotas del usuario                |
| GET    | `/pets/{id}`               | Detalle de una mascota (con rutinas, racha…)  |
| POST   | `/pets`                    | Crea una mascota                              |
| PUT    | `/pets/{id}`               | Actualiza una mascota                         |
| DELETE | `/pets/{id}`               | Elimina una mascota                           |
| PATCH  | `/pets/{id}/streak`        | Marca la rutina del día y actualiza la racha  |
| CRUD   | `/pets/{petId}/routines`   | Rutinas (GET/POST) + `PATCH …/complete`       |
| CRUD   | `/pets/{petId}/reminders`  | Recordatorios (GET/POST) + `PATCH …/complete` |
| CRUD   | `/pets/{petId}/vaccinations`| Vacunas (GET/POST/PUT/DELETE)                |

### Razas e imágenes (público, la clave de API vive en el backend)
| Método | Ruta                   | Descripción                                  |
|--------|------------------------|----------------------------------------------|
| GET    | `/breeds/dogs?q=`      | Buscador de razas de perros (proxy API Ninjas)|
| GET    | `/breeds/cats?q=`      | Buscador de razas de gatos (proxy API Ninjas)|
| GET    | `/dogs/image?breed=`   | Imagen de perro por raza (dog.ceo)           |
| GET    | `/dogs/random`         | Imagen aleatoria de perro                    |
| GET    | `/cats/image?breedId=` | Imagen de gato por raza (The Cat API)        |
| GET    | `/cats/random`         | Imagen aleatoria de gato                     |

Ejemplo de respuesta de `/pets/{id}` (extracto):

```json
{
  "id": 1,
  "name": "Luna",
  "species": "Perro",
  "dailyStreak": 5,
  "bestStreak": 12,
  "streakStatus": "active",
  "routineDoneToday": false,
  "reminders": [
    { "id": 3, "title": "Vacuna anual", "reminderDate": "2026-06-20T09:00:00", "status": "proximo", "daysUntil": 2, "completed": false }
  ]
}
```

## Tests / Pruebas

```bash
mvn test
```

Incluye `BackendApplicationTests` con la prueba de contexto de Spring Boot.

## Estructura del equipo / Autores

- **Maura Ramírez Navarro** — `mau.ramirezn@duocuc.cl` — Desarrollo backend y frontend, organización de tareas.
- **Allison Sepúlveda** — `all.sepulveda@duocuc.cl` — Documentación, redacción de informes técnicos y levantamiento de requisitos.

## Licencia

Proyecto académico sin licencia comercial definida. Uso educativo.
