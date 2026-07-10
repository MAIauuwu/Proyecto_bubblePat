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

## Despliegue (producción) por localhost.                                                                                                                                                                               
   ## Preparación del código (ya aplicada)                                                                                                                                                         
     Para funcionar en la nube, el código se adaptó en los siguientes puntos:                                                                                                                                                                                                                                                                                                            
     1. Puerto configurable en el backend — en application.properties:                                                                                                                       
        server.port=${PORT:8081} EC2 publica el servicio en 8081; la variable PORT queda como override opcional.         
        
     2. CORS configurable — los orígenes permitidos se controlan con CORS_ALLOWED_ORIGINS (en SecurityConfig), para autorizar el dominio/IP pública del                         
        frontend. Cada vez que la IP de la EC2 cambia, se actualiza este valor en el .env.                         
        
     3. URL del backend configurable en el frontend — src/api/client.js usa VITE_API_BASE_URL si está definida; si no, mantiene el proxy /api de Vite (           
        desarrollo local).                                           
        
     4. Build multi-etapa del backend — el Dockerfile compila con Maven + JDK 17 y luego ejecuta el .jar con un JRE 17 liviano, reduciendo el tamaño de la          
        imagen final.                                                                                                                                             
                                                                                                                                                                                                         
## Pasos del despliegue                                                                                                                                                                                                                                                                                                                                                                   
     1. Lanzar la instancia desde la consola de EC2: AMI Amazon Linux 2023, tipo t3.micro, key pair .pem y un Security Group que abra los puertos 22 (SSH) y                                 8081 (backend) a internet.                                                                                                                                                                       
     2. Instalar Docker en la instancia (dnf install docker) y agregar al usuario ec2-user al grupo docker.                                                                                              
     3. Levantar PostgreSQL como contenedor de la imagen oficial postgres:18, con variables POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD y un volumen para                                   persistir los datos.                                                                                                                                                                             
     4. Clonar el repositorio (git clone), crear el .env con vim con todas las credenciales y construir la imagen del backend con docker build.                                                          
     5. Ejecutar el backend en la misma red Docker, pasando el .env con --env-file y publicando el puerto 8081.                                                                                          
     6. Automatizar con scripts (start.sh, rebuild.sh) guardados con vim en ~/scripts/ y hechos ejecutables con chmod +x. Los contenedores usan --restart                                      unless-stopped, por lo que se reactivan solos tras un reinicio de la instancia.                                                                                                                  
     7. Publicar la URL del backend como http://<IP-PÚBLICA-ACTUAL>:8081/api, actualizándola cuando la IP cambie.                                                                            
                                                                                        
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
- **Allison Sepúlveda** — `all.sepulveda@duocuc.cl` — Documentación, redacción de informes técnicos, levantamiento de requisitos y QA.

## Tests / Pruebas

```bash
cd backend
mvn test
```

Incluye la prueba de contexto de Spring Boot.

## Licencia

Proyecto académico sin licencia comercial definida. Uso educativo.
