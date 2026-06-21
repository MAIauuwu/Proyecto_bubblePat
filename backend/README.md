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

## Despliegue en AWS (EC2)

El backend se publica en una instancia **Amazon EC2** corriendo **Amazon Linux 2023** (tipo **`t3.micro`**, Free Tier). Todo el stack corre con **Docker**: el backend en un contenedor construido a partir del `Dockerfile` del proyecto y **PostgreSQL en otro contenedor** (imagen oficial `postgres`), conectados entre sí por una red de Docker. No se usa Nginx: la API se expone directamente en el puerto **8081** de la IP pública de la instancia.

Topología:

```
                    EC2 (Amazon Linux 2023, t3.micro)
Internet ──:8081──> Docker (bridge "bubblepat-net")
                        ├── contenedor "postgres"  :5432
                        └── contenedor "bubblepat" :8081  (Spring Boot / JRE 17)
```

### 1) Lanzar la instancia EC2

1. Consola AWS → **EC2 → Launch instance**.
2. Nombre: `bubblepat-backend`.
3. AMI: **Amazon Linux 2023**.
4. Tipo: **t3.micro** (Free Tier).
5. Key pair: crear / elegir un `.pem`.
6. Security Group con reglas **inbound**:

   | Puerto | Origen    | Motivo           |
   |--------|-----------|------------------|
   | 22     | Mi IP     | SSH              |
   | 8081   | 0.0.0.0/0 | Backend (público)|

7. Lanzar la instancia.

> **IPv4 pública dinámica:** esta EC2 **no** tiene Elastic IP asociada, por lo que la IP pública cambia **cada vez que la instancia se detiene/arranca**. Hay que consultar la IP vigente en la consola de AWS (o con el comando de la sección 8) cada vez que se reinicia, y actualizarla en `CORS_ALLOWED_ORIGINS` del `.env` y en la URL del frontend.

### 2) Conectarse por SSH

```bash
chmod 400 bubblepat-key.pem
ssh -i "bubblepat-key.pem" ec2-user@<IP-PÚBLICA-ACTUAL>
```

### 3) Instalar Docker

```bash
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl enable --now docker
sudo usermod -aG docker ec2-user
exit   # y reconectar por SSH para que tome el grupo docker
```

### 4) Crear la red y levantar PostgreSQL (contenedor)

```bash
docker network create bubblepat-net

docker run -d \
  --name postgres \
  --network bubblepat-net \
  --restart unless-stopped \
  -e POSTGRES_DB=bubblepat_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=<TU_PASSWORD> \
  -v pgdata:/var/lib/postgresql/data \
  postgres:18
```

- `-v pgdata:...` **persiste** la base de datos aunque el contenedor se borre.
- El backend se conectará a este contenedor por su nombre de host (`postgres`) dentro de la red `bubblepat-net`.

### 5) Clonar el repo, definir variables y construir la imagen

```bash
git clone https://github.com/MAIauuwu/Proyecto_bubblePat.git
cd Proyecto_bubblePat/backend

# crear el .env con los valores de producción
cp .env.example .env
vim .env
```

Contenido del `.env` (los nombres coinciden con `application.properties`):

```env
DB_URL=jdbc:postgresql://postgres:5432/bubblepat_db
DB_USER=postgres
DB_PASSWORD=<TU_PASSWORD>
API_NINJAS_KEY=tu_api_ninjas_key
JWT_SECRET=cadena-secreta-larga-y-aleatoria-para-jwt
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://<IP-PÚBLICA-ACTUAL>:8081
```

> Ojo: en `DB_URL` se usa el **nombre del contenedor** (`postgres`) y no `localhost`, porque el backend corre en su propio contenedor dentro de `bubblepat-net`.

Construir la imagen:

```bash
docker build -t bubblepat-backend .
```

### 6) Levantar el backend (conectado a PostgreSQL)

```bash
docker run -d \
  --name bubblepat \
  --network bubblepat-net \
  --restart unless-stopped \
  --env-file .env \
  -p 8081:8081 \
  bubblepat-backend
```

Verificar:

```bash
docker ps
docker logs -f bubblepat
curl http://localhost:8081/api/dogs/random
```

### 7) Scripts guardados con `vim`

Para no recordar los comandos largos de Docker, se dejaron scripts en la instancia editados con `vim`:

`~/scripts/start.sh` — levanta la red, PostgreSQL y el backend:

```bash
#!/bin/bash
set -e
docker network create bubblepat-net 2>/dev/null || true
docker start postgres   2>/dev/null || docker run -d --name postgres --network bubblepat-net \
    --restart unless-stopped -e POSTGRES_DB=bubblepat_db \
    -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=<TU_PASSWORD> \
    -v pgdata:/var/lib/postgresql/data postgres:18
docker start bubblepat  2>/dev/null || docker run -d --name bubblepat --network bubblepat-net \
    --restart unless-stopped --env-file ~/Proyecto_bubblePat/backend/.env \
    -p 8081:8081 bubblepat-backend
```

`~/scripts/rebuild.sh` — reconstruye y reinicia el backend tras un cambio:

```bash
#!/bin/bash
set -e
cd ~/Proyecto_bubblePat/backend
git pull
docker build -t bubblepat-backend .
docker stop bubblepat 2>/dev/null || true
docker rm   bubblepat 2>/dev/null || true
docker run -d --name bubblepat --network bubblepat-net \
    --restart unless-stopped --env-file .env -p 8081:8081 bubblepat-backend
docker logs -f bubblepat
```

Se crean con `vim ~/scripts/start.sh`, se pega el contenido, se guardan con `:wq` y se hacen ejecutables:

```bash
chmod +x ~/scripts/*.sh
~/scripts/start.sh
```

> Como los contenedores se crearon con `--restart unless-stopped`, el backend y PostgreSQL se levantan automáticamente al reiniciar la EC2 sin necesidad de invocar el script.

### 8) Endpoint público

La API queda accesible desde internet por la **IP pública vigente** de la instancia, en el puerto `8081`:

```
http://<IP-PÚBLICA-ACTUAL>:8081/api
```

Como la instancia no tiene Elastic IP, la IPv4 pública **cambia en cada arranque**. Para averiguar la IP vigente desde dentro de la propia EC2:

```bash
curl http://checkip.amazonaws.com
# o también:
curl -s http://169.254.169.254/latest/meta-data/public-ipv4
```

Tras obtener la nueva IP, actualizar:

1. El `CORS_ALLOWED_ORIGINS` dentro de `~/Proyecto_bubblePat/backend/.env` (con `vim`).
2. El cliente REST / baseURL del **frontend** que apunta al backend.
3. Reiniciar el contenedor: `docker restart bubblepat`.

### Comandos útiles de operación

```bash
docker ps                          # contenedores activos
docker logs -f bubblepat           # logs del backend
docker logs -f postgres            # logs de la BD
docker restart bubblepat           # reiniciar backend
docker exec -it postgres psql -U postgres -d bubblepat_db   # entrar a la BD
~/scripts/rebuild.sh               # actualizar imagen tras un nuevo commit
sudo dnf update -y                 # actualizar paquetes del SO
```

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
