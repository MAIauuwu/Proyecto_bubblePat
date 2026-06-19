# BubblePat

App web de gestión de cuidado de mascotas: fichas médicas, rutinas diarias con sistema de **rachas**, recordatorios y vacunas. Genera imágenes e información de razas integrando APIs externas (dog.ceo, The Cat API, API Ninjas).

## Descripción

BubblePat ayuda a quienes tienen mascotas a llevar un registro del cuidado diario: alimentación, paseos, medicinas y baños, premiando la constancia con una **racha de días consecutivos** (y su récord). Además permite agendar **recordatorios** (citas veterinarias, baños, vacunas) con estados de urgencia (vencido / hoy / próximo) y llevar la **ficha médica** con vacunas y datos clínicos.

## Tecnologías utilizadas

**Backend**
- Java 17
- Spring Boot 3.5 (Spring Web, Spring Data JPA, Spring Security, Validation)
- PostgreSQL
- JWT (jjwt) para autenticación
- Lombok
- APIs externas: dog.ceo, The Cat API, API Ninjas

**Frontend**
- React 19 + Vite 8
- Tailwind CSS 4
- React Router 7
- Axios (proxy hacia el backend)

## Requisitos previos

- JDK 17+
- Maven 3.9+
- Node.js 18+
- PostgreSQL (base de datos `bubblepat_db`)
- Una clave de [API Ninjas](https://api-ninjas.com) para el buscador de razas

## Instalación

```bash
git clone https://github.com/MAIauuwu/Proyecto_bubblePat.git
cd Proyecto_bubblePat

# Backend
cd backend
mvn clean compile
cd ..

# Frontend
cd frontend
npm install
cd ..
```

## Configuración

El backend lee sus credenciales desde variables de entorno o un archivo `.env` (no se commitean).

```bash
cd backend
cp .env.example .env      # Windows: copy .env.example .env
```

Edita `backend/.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/bubblepat_db
DB_USER=postgres
DB_PASSWORD=tu_password_de_postgres
API_NINJAS_KEY=tu_api_ninjas_key
JWT_SECRET=cadena-secreta-larga-y-aleatoria-para-jwt
```

El frontend en desarrollo **no** requiere configuración: Vite hace proxy de `/api` hacia `http://localhost:8081` (ver `frontend/vite.config.js`).

## Uso / Ejecución

```bash
# 1) Crear la base de datos (una sola vez)
createdb -U postgres bubblepat_db

# 2) Levantar el backend (puerto 8081)
cd backend
mvn spring-boot:run

# 3) En otra terminal, levantar el frontend (puerto 3000)
cd frontend
npm run dev
```

- Backend: http://localhost:8081
- Frontend: http://localhost:3000
- El esquema de tablas se crea/actualiza automáticamente (`spring.jpa.hibernate.ddl-auto=update`).

## Despliegue (producción)

### Plataforma elegida: **Render**

El proyecto se despliega en [Render](https://render.com), una plataforma PaaS (Platform as a Service) que aloja los tres componentes del sistema —base de datos, backend y frontend— en una misma cuenta y panel, lo que simplifica enormemente la operación respecto a combinar varios proveedores.

**Justificación de la elección:**

- **Todo en uno:** soporta PostgreSQL gestionado, web services (Java/Spring Boot) y sitios estáticos (frontend Vite) bajo el mismo proveedor. No es necesario coordinar entre Vercel + Railway + Neon, por ejemplo.
- **Integración nativa con GitHub:** cada `push` desencadena un redeploy automático del servicio afectado, ideal para un flujo de trabajo ágil.
- **Capa gratuita permanente:** permite mantener el proyecto en línea para la entrega académica sin costo, con PostgreSQL gratuito por 90 días y web/static services en plan Free.
- **Detección automática del stack:** reconoce Maven (`pom.xml`) y Node/Vite sin configuración manual del runtime.
- **Variables de entorno seguras:** las credenciales (`DB_*`, `JWT_SECRET`, `API_NINJAS_KEY`, `CORS_ALLOWED_ORIGINS`, `VITE_API_BASE_URL`) viven en el panel, nunca en el repositorio.
- **Curva de aprendizaje baja:** interfaz web sencilla, comparable a Heroku, suficiente para un proyecto académico.

> **Trade-off del plan Free:** los servicios "duermen" tras 15 minutos sin tráfico; el primer request tarda ~30–50 s en despertar. Es aceptable para fines demostrativos.

### Servicios en Render

| Servicio | Tipo | Carpeta | Rol |
|----------|------|---------|-----|
| `bubblepat-db` | PostgreSQL | — | Base de datos relacional gestionada |
| `bubblepat-backend` | Web Service | `backend/` | API REST (Spring Boot) |
| `bubblepat-frontend` | Static Site | `frontend/` | SPA (React + Vite) publicada desde `dist/` |

### Preparación del código (ya aplicada)

Para funcionar en la nube, el código se adaptó en los siguientes puntos:

1. **Puerto dinámico en el backend** — Render inyecta el puerto por la variable `PORT`. En `application.properties`:
   ```properties
   server.port=${PORT:8081}
   ```
2. **CORS configurable** — los orígenes permitidos se controlan con `CORS_ALLOWED_ORIGINS` (en `SecurityConfig`), para autorizar el dominio público del frontend.
3. **URL del backend configurable en el frontend** — `src/api/client.js` usa `VITE_API_BASE_URL` si está definida; si no, mantiene el proxy `/api` de Vite (desarrollo local).

### Pasos del despliegue

1. **Crear la base de datos**
   - Render → *New +* → **PostgreSQL** → plan Free.
   - Anotar `Host`, `Database`, `Username`, `Password` (Render también entrega una *Internal Database URL*).

2. **Desplegar el backend (Web Service)**
   - Conectar el repo `MAIauuwu/Proyecto_bubblePat`, carpeta raíz `backend`.
   - **Build Command:** `./mvnw clean install -DskipTests`
   - **Start Command:** `java -jar target/*.jar`
   - Variables de entorno:

     | Key | Value |
     |-----|-------|
     | `DB_URL` | `jdbc:postgresql://<host>:5432/<database>` |
     | `DB_USER` | *(de Render PostgreSQL)* |
     | `DB_PASSWORD` | *(de Render PostgreSQL)* |
     | `API_NINJAS_KEY` | clave real |
     | `JWT_SECRET` | cadena aleatoria de 40+ caracteres |
     | `CORS_ALLOWED_ORIGINS` | `https://bubblepat-frontend.onrender.com` |

3. **Desplegar el frontend (Static Site)**
   - Mismo repo, carpeta raíz `frontend`.
   - **Build Command:** `npm install && npm run build`
   - **Publish Directory:** `dist`
   - Variable de entorno:

     | Key | Value |
     |-----|-------|
     | `VITE_API_BASE_URL` | `https://bubblepat-backend.onrender.com/api` |

4. **Cerrar el CORS**
   - Volver al backend → Environment → confirmar que `CORS_ALLOWED_ORIGINS` coincide con la URL final del Static Site.

## Arquitectura del proyecto

Monorepo con frontend y backend separados en carpetas:

```
ProyectoBubblePat/
├── backend/                # API REST (Spring Boot)
│   └── src/main/java/com/bubblepat/backend/
│       ├── Main.java       # Arranque + carga de .env
│       ├── config/         # Configuración (RestTemplate)
│       ├── controller/     # Controladores REST
│       ├── dto/            # Objetos de transferencia
│       ├── exception/      # Manejo global de errores
│       ├── model/          # Entidades JPA (User, Pet, Routine, Vaccination, Reminder)
│       ├── repository/     # Spring Data JPA
│       ├── security/       # JWT, filtros y configuración de Security
│       └── service/        # Lógica de negocio
├── frontend/               # SPA (React + Vite)
│   └── src/
│       ├── api/            # Cliente axios + helpers de razas
│       ├── context/        # AuthContext (login/registro/JWT)
│       ├── pages/          # Login, Register, Dashboard, PetForm, PetDetail
│       ├── assets/         # Logo e imágenes
│       ├── App.jsx         # Rutas
│       └── main.jsx        # Punto de entrada
└── docs/                   # Documentación e informes del proyecto
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
| Método | Ruta             | Descripción         |
|--------|------------------|---------------------|
| POST   | `/auth/register` | Registro de usuario |
| POST   | `/auth/login`    | Login, devuelve JWT |

### Mascotas (`/pets`) — requiere JWT
| Método | Ruta                        | Descripción                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/pets`                     | Lista las mascotas del usuario               |
| GET    | `/pets/{id}`                | Detalle de una mascota (rutinas, racha…)     |
| POST   | `/pets`                     | Crea una mascota                             |
| PUT    | `/pets/{id}`                | Actualiza una mascota                        |
| DELETE | `/pets/{id}`                | Elimina una mascota                          |
| PATCH  | `/pets/{id}/streak`         | Marca la rutina del día y actualiza la racha |
| CRUD   | `/pets/{petId}/routines`    | Rutinas (GET/POST) + `PATCH …/complete`      |
| CRUD   | `/pets/{petId}/reminders`   | Recordatorios (GET/POST) + `PATCH …/complete`|
| CRUD   | `/pets/{petId}/vaccinations`| Vacunas (GET/POST/PUT/DELETE)                |

### Razas e imágenes (público, la clave de API vive en el backend)
| Método | Ruta                  | Descripción                                   |
|--------|-----------------------|-----------------------------------------------|
| GET    | `/breeds/dogs?q=`     | Buscador de razas de perros (proxy API Ninjas)|
| GET    | `/breeds/cats?q=`     | Buscador de razas de gatos (proxy API Ninjas) |
| GET    | `/dogs/image?breed=`  | Imagen de perro por raza (dog.ceo)            |
| GET    | `/dogs/random`        | Imagen aleatoria de perro                     |
| GET    | `/cats/image?breedId=`| Imagen de gato por raza (The Cat API)         |
| GET    | `/cats/random`        | Imagen aleatoria de gato                      |

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

## Estructura del equipo / Autores

- **Maura Ramírez Navarro** — `mau.ramirezn@duocuc.cl` — Desarrollo backend y frontend, organización de tareas.
- **Allison Sepúlveda** — `all.sepulveda@duocuc.cl` — Documentación, redacción de informes técnicos y levantamiento de requisitos.

## Tests / Pruebas

```bash
cd backend
mvn test
```

Incluye la prueba de contexto de Spring Boot.

## Licencia

Proyecto académico sin licencia comercial definida. Uso educativo.
