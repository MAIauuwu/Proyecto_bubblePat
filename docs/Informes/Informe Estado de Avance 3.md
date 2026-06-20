# INFORME — ESTADO DE AVANCE N°3
## Proyecto: BubblePat — Sistema de gestión de cuidado de mascotas

**Asignatura:** TPY1101 — Taller Aplicado de Programación
**Evaluación:** Parcial N°3 (Encargo + Presentación) — 35%ponderación

**Integrantes:**
- **Maura Ramírez Navarro** — `mau.ramirezn@duocuc.cl` — Desarrollo backend y frontend, organización de tareas.
- **Allison Sepúlveda Arenas** — `all.sepulveda@duocuc.cl` — Documentación, redacción de informes técnicos y levantamiento de requisitos.

**Docente guía:** [INSERTAR NOMBRE DOCENTE]
**Sección:** [INSERTAR SECCIÓN, ej. 001D]
**Fecha:** Junio, 2026
**Repositorio:** https://github.com/MAIauuwu/Proyecto_bubblePat

---

## ÍNDICE

1. **Introducción**
2. **Desarrollo — Evaluación Parcial 1**
   - 2.1 Descripción y contexto del proyecto
   - 2.2 Problemática, objetivos y alcance
   - 2.3 Metodología y planificación
   - 2.4 Arquitectura y tecnologías seleccionadas
3. **Desarrollo — Evaluación Parcial 2**
   - 3.1 Diagramas y documentos de diseño
   - 3.2 Ambiente de pruebas
   - 3.3 Backup y configuración del servidor Cloud
   - 3.4 Código fuente y buenas prácticas
4. **Desarrollo — Evaluación Parcial 3** *(foco de esta entrega)*
   - 4.1 Plan de pruebas de software (IL3.1)
   - 4.2 Base de datos de pruebas (IL3.1)
   - 4.3 Aplicación y resultados de las pruebas (IL3.1)
   - 4.4 Evidencia de configuración y respaldo (IL3.1)
   - 4.5 Mejoras realizadas al producto (IL3.2)
   - 4.6 Documentos de aceptación
5. **Conclusiones y lecciones aprendidas (IL3.3)**
6. **Anexos**

---

## 1. Introducción

El presente informe corresponde al **Estado de Avance N°3** del proyecto **BubblePat**, desarrollado en la asignatura TPY1101. BubblePat es una aplicación web de gestión del cuidado de mascotas que centraliza fichas médicas, rutinas diarias con sistema de **rachas**, recordatorios y vacunas, enriquecida con información e imágenes de razas obtenidas desde APIs externas (dog.ceo, The Cat API y API Ninjas).

El propósito de este documento es presentar la **totalidad de la documentación del proyecto**, consolidando los avances de las Evaluaciones Parciales 1 y 2 (actualizadas) y profundizando, en esta entrega, en el **plan de pruebas de software**, la **base de datos de pruebas**, la **ejecución y resultados**, las **evidencias de configuración y respaldo**, las **mejoras aplicadas** derivadas de las pruebas y las **conclusiones y lecciones aprendidas**, dando cumplimiento a los indicadores de logro IL3.1, IL3.2 e IL3.3.

La solución está construida con un **backend en Java 17 / Spring Boot 3.5** (API REST con seguridad JWT y persistencia en PostgreSQL) y un **frontend en React 19 + Vite + Tailwind CSS**.

---

## 2. Desarrollo — Evaluación Parcial 1

### 2.1 Descripción y contexto del proyecto (cliente o mercado objetivo)

**BubblePat** es una plataforma web dirigida a **dueños de mascotas** que necesitan llevar un registro ordenado y centralizado del cuidado diario de sus animales. El mercado objetivo está conformado por personas con tenencia responsable de perros y gatos que, actualmente, gestionan esta información de forma manual (agendas, notas o memoria), lo que provoca olvidos y desorganización.

La plataforma permite:
- Registrar **mascotas** asociadas a cada usuario, con datos clínicos (peso, alergias, desparasitación).
- Llevar **rutinas diarias** (alimentación, paseo, medicina, baño) premiando la constancia con una **racha de días consecutivos** y su **récord histórico**.
- Agendar **recordatorios** (citas veterinarias, baños, vacunas) con estados de urgencia calculados automáticamente (vencido / hoy / próximo / futuro).
- Administrar la **ficha médica** con vacunas aplicadas y próximas dosis.
- Consultar **información e imágenes de razas** integrando APIs externas.

**Stakeholders principales:**

| Stakeholder | Descripción |
|---|---|
| Dueños de mascotas | Usuarios finales del sistema |
| Mascotas | Beneficiarias indirectas (mejor cuidado) |
| Veterinarios | Usuarios futuros / fuente de datos clínicos |
| Equipo de desarrollo | Diseño e implementación |
| Institución académica (Duoc UC) | Entidad evaluadora |

### 2.2 Problemática, objetivos y alcance

#### 2.2.1 Problemática
El aumento en la tenencia de mascotas ha incrementado la necesidad de llevar un control adecuado sobre su salud y bienestar. Sin embargo, muchos dueños no cuentan con herramientas ni conocimientos suficientes para gestionar información clave como ciclos biológicos, vacunas, controles veterinarios y hábitos de cuidado. La información se maneja de forma **manual, dispersa o dependiente de la memoria**, lo que provoca olvidos de controles importantes, incumplimiento de calendarios de salud y detección tardía de problemas.

**Causas:** uso de métodos manuales, falta de herramientas digitales especializadas, información dispersa, ausencia de recordatorios automatizados.
**Impacto:** omisión de cuidados (estimado 30–40%), pérdida de información relevante, 1–2 horas semanales perdidas en gestión, riesgos en la salud de las mascotas.

#### 2.2.2 Objetivo general
Desarrollar una plataforma digital que permita mejorar la **adherencia a las rutinas de cuidado de mascotas**, mediante la centralización de información médica, hábitos y recordatorios, con el propósito de reducir omisiones en el cuidado y contribuir al bienestar, la salud y la calidad de vida tanto de las mascotas como de sus dueños, apoyándose en APIs externas para enriquecer la información.

#### 2.2.3 Objetivos específicos
- Implementar el **registro y gestión de usuarios y mascotas**.
- Desarrollar un **módulo de fichas médicas digitales** (vacunas, datos clínicos).
- Implementar un **sistema de recordatorios** con estados de urgencia.
- Desarrollar funcionalidades de **seguimiento de hábitos** mediante rachas.
- **Garantizar la seguridad y persistencia** de los datos (JWT + bcrypt + PostgreSQL).
- **Integrar datos externos** (dog.ceo, The Cat API, API Ninjas) para razas, características e imágenes.

#### 2.2.4 Alcance
**Incluye:** gestión de usuarios, gestión de mascotas, registro de fichas médicas, sistema de recordatorios, seguimiento de hábitos (rachas), integración de APIs externas.
**Exclusiones:** no incluye aplicación móvil nativa en esta fase, no incluye integración con veterinarias externas, no incluye sistema de pagos, las notificaciones no son push en tiempo real (se basan en recordatorios programados).

#### 2.2.5 Requerimientos funcionales (RF)

| ID | Requisito | Descripción |
|---|---|---|
| RF01 | Registro de usuarios | Registro mediante correo y contraseña |
| RF02 | Autenticación | Login con emisión de JWT |
| RF03 | Registro de mascotas | Registrar mascotas asociadas a un usuario |
| RF04 | Gestión de mascotas | Editar y eliminar mascotas |
| RF05 | Ficha médica | Registrar vacunas y datos clínicos |
| RF06 | Recordatorios | Crear recordatorios con estado de urgencia |
| RF07 | Seguimiento de hábitos | Rutinas diarias con sistema de rachas |
| RF08 | Historial de mascota | Visualizar historial completo (rutinas, vacunas, recordatorios) |
| RF09 | Cálculo de estados | Estados automáticos vencido/hoy/próximo/futuro |
| RF10 | Consumo de APIs externas | Razas e imágenes (dog.ceo, The Cat API, API Ninjas) |

**Reglas de negocio:**
- **RN01:** Un usuario solo puede visualizar y administrar sus propias mascotas (validación por `email` del token).
- **RN02:** Toda mascota pertenece obligatoriamente a un usuario registrado.
- **RN03:** La rutina del día solo puede completarse una vez al día.
- **RN04:** La racha avanza solo cuando **todas** las rutinas del día están completas.

#### 2.2.6 Requerimientos no funcionales (RNF)

| ID | Requisito | Descripción |
|---|---|---|
| RNF01 | Rendimiento | Respuestas en máximo 3 s en condiciones normales |
| RNF02 | Seguridad | JWT, bcrypt, HTTPS |
| RNF03 | Disponibilidad | Mínimo 99% anual |
| RNF04 | Usabilidad | Interfaz intuitiva y consistente |
| RNF05 | Escalabilidad | Capacidad de crecer en usuarios/datos |
| RNF06 | Compatibilidad | Accesible desde navegadores web |

### 2.3 Metodología y planificación

El proyecto se desarrolló bajo un **enfoque ágil adaptado (Scrum)**, organizando el trabajo en **sprints** cortos e iterativos que permitieron adaptarse a cambios, detectar errores en etapas tempranas y distribuir tareas entre los integrantes. Se utilizó **GitHub** para el control de versiones y documentación colaborativa.

| Fase | Nombre | Actividades |
|---|---|---|
| Fase 1 | Análisis | Levantamiento de requerimientos, contexto, alcance |
| Fase 2 | Diseño | Arquitectura, base de datos, diagramas, tecnologías |
| Fase 3 | Desarrollo | Implementación de funcionalidades e integraciones |
| Fase 4 | Pruebas | Pruebas funcionales, corrección de errores, validación |
| Fase 5 | Documentación | Informes técnicos, manuales, evidencias |

[INSERTAR EVIDENCIA: captura del tablero de GitHub Projects / commits / cronograma Gantt]

### 2.4 Arquitectura y tecnologías seleccionadas (stack, justificación, Cloud)

BubblePat se implementa como una **aplicación monolítica en capas** (frontend + backend separados) con comunicación **REST/JSON**. Aunque en EP1 se proyectó una arquitectura de microservicios, el diseño final consolidó la lógica en un backend Spring Boot modular con responsabilidades claramente separadas por capas y por dominio (auth, pets, rutinas, vacunas, recordatorios, integración externa), manteniendo el desacoplamiento y la mantenibilidad como principios fundamentales.

**Stack tecnológico:**

| Capa | Tecnología | Versión |
|---|---|---|
| Backend | Java + Spring Boot | 17 / 3.5 |
| Seguridad | Spring Security + JWT (jjwt) | 0.12.6 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | PostgreSQL | 18 |
| Frontend | React + Vite | 19 / 8 |
| Estilos | Tailwind CSS | 4 |
| Routing | React Router | 7 |
| HTTP cliente | Axios | 1.16 |
| APIs externas | dog.ceo, The Cat API, API Ninjas | — |
| Control de versiones | Git + GitHub | — |
| Pruebas API | Postman | — |

**Justificación:** Spring Boot provee un ecosistema maduro para APIs REST seguras y persistencia; PostgreSQL garantiza integridad referencial y escalabilidad; React + Tailwind permiten una interfaz moderna, responsiva y de rápida iteración; las APIs externas evitan mantener catálogos de razas/imágenes localmente, reduciendo costo de mantenimiento.

**Entorno Cloud / despliegue:** el sistema está diseñado para despliegue web. La base de datos y el servidor se configuran según se detalla en la sección 3.3 (evidencia de respaldo y configuración).

[INSERTAR EVIDENCIA: captura del diagrama de arquitectura / despliegue]

---

## 3. Desarrollo — Evaluación Parcial 2

### 3.1 Diagramas y documentos de diseño (ER, casos de uso, flujo, despliegue)

#### 3.1.1 Modelo de datos (ERD)
PostgreSQL. Tablas generadas automáticamente por JPA (`ddl-auto=update`). Relaciones: un `User` (1)→(N) `Pet`; un `Pet` (1)→(N) `Routine`, `Reminder`, `Vaccination`.

| Tabla | Descripción | Campos clave |
|---|---|---|
| `users` | Usuarios autenticados | id, email (único), password (hash bcrypt), name, created_at |
| `pets` | Mascotas | id, name, species, breed, birth_date, weight, allergic_to, last_deworming, **daily_streak**, **best_streak**, **last_routine_date**, user_id |
| `routines` | Rutinas diarias | id, pet_id, type, description, completed, completed_at |
| `reminders` | Recordatorios | id, pet_id, title, description, reminder_date, completed |
| `vaccinations` | Vacunas | id, pet_id, name, applied_date, next_dose_date, vet_name, notes |

[INSERTAR EVIDENCIA: diagrama entidad-relación (ERD) y captura del esquema creado en PostgreSQL]

#### 3.1.2 Diagrama de clases del dominio
Las entidades JPA (`User`, `Pet`, `Routine`, `Reminder`, `Vaccination`) con sus relaciones `@OneToMany`/`@ManyToOne` y *cascade* + *orphanRemoval* conforman el modelo orientado a objetos.

[INSERTAR EVIDENCIA: diagrama de clases UML]

#### 3.1.3 Casos de uso principales

| Caso de uso | Actor | Descripción |
|---|---|---|
| Registrarse / Iniciar sesión | Usuario | Autenticación con JWT |
| Registrar mascota | Usuario | Crea mascota asociada a su cuenta |
| Editar / Eliminar mascota | Usuario | Gestiona sus mascotas |
| Completar rutina del día | Usuario | Marca rutina y avanza racha |
| Registrar vacuna | Usuario | Agrega vacuna a la ficha médica |
| Crear recordatorio | Usuario | Agenda recordatorio con fecha/hora |
| Consultar razas/imágenes | Usuario | Búsqueda vía APIs externas |

[INSERTAR EVIDENCIA: diagrama de casos de uso y de secuencia (Registrar mascota, Completar rutina)]

### 3.2 Ambiente de pruebas

**Entorno de desarrollo y pruebas:**

| Componente | Detalle |
|---|---|
| SO | Windows 11 |
| JDK | 17 |
| Build backend | Maven 3.9 (`mvn spring-boot:run`) |
| Backend | http://localhost:8081 |
| Frontend | http://localhost:3000 (Vite dev, proxy `/api` → 8081) |
| Base de datos | PostgreSQL 18, BD `bubblepat_db` |
| Herramientas | Postman (colección de endpoints), navegador + DevTools, consola |

El esquema se crea/actualiza automáticamente al levantar el backend. El frontend hace proxy de `/api` hacia el backend (ver `frontend/vite.config.js`), por lo que no requiere configuración extra en desarrollo.

### 3.3 Backup y configuración del servidor Cloud

> **Nota de la cadena lógica exigida por la coordinación (IL3.1):** la evidencia de las copias de configuración solicitadas en EP2 debe aparecer aquí con capturas reales (backup ejecutado, servidor configurado, instalación de lenguajes/bibliotecas).

**Variables de entorno (backend `.env`):**

```env
DB_URL=jdbc:postgresql://localhost:5432/bubblepat_db
DB_USER=postgres
DB_PASSWORD=tu_password_de_postgres
API_NINJAS_KEY=tu_api_ninjas_key
JWT_SECRET=cadena-secreta-larga-y-aleatoria-para-jwt
```

Estas variables se inyectan en `application.properties` mediante placeholders `${VAR}` y **no se commitean** (están en `.gitignore`; existe `.env.example`).

[INSERTAR EVIDENCIA: captura del backup de la BD ejecutado con `pg_dump` y archivo `.sql` generado]

```bash
# Ejemplo de comando de respaldo utilizado
pg_dump -U postgres -d bubblepat_db -F c -f bubblepat_backup.dump
# Restauración de prueba
pg_restore -U postgres -d bubblepat_db_test -c bubblepat_backup.dump
```

[INSERTAR EVIDENCIA: captura del servidor configurado (PostgreSQL levantado, puertos, instalación de JDK 17 / Node.js 18)]

### 3.4 Código fuente y buenas prácticas

La estructura del repositorio es un monorepo con `backend/` y `frontend/` separados.

```
ProyectoBubblePat/
├── backend/                # API REST (Spring Boot)
│   └── src/main/java/com/bubblepat/backend/
│       ├── Main.java            # Arranque + carga de .env
│       ├── config/              # RestTemplateConfig
│       ├── controller/          # Auth, Pet, Dog, Cat, BreedSearch
│       ├── dto/                 # Request/Response (validación con @Valid)
│       ├── exception/           # GlobalExceptionHandler (@RestControllerAdvice)
│       ├── model/               # Entidades JPA (User, Pet, Routine, Vaccination, Reminder)
│       ├── repository/          # Spring Data JPA
│       ├── security/            # JwtUtil, JwtAuthFilter, SecurityConfig
│       └── service/             # AuthService, PetService, BreedSearch, DogApi, CatApi, BreedInfo
├── frontend/               # SPA (React + Vite)
│   └── src/
│       ├── api/            # cliente axios + helpers de razas
│       ├── context/        # AuthContext (login/registro/JWT en localStorage)
│       ├── pages/          # Login, Register, Dashboard, PetForm, PetDetail
│       ├── assets/         # Logo e imágenes
│       ├── App.jsx         # Rutas protegidas
│       └── main.jsx
└── docs/                   # Documentación e informes
```

**Buenas prácticas aplicadas:**
- **Separación por capas** (controller → service → repository) y responsabilidades únicas.
- **Seguridad JWT *stateless***: endpoints públicos limitados (`/api/auth/**`, `/api/dogs/**`, `/api/cats/**`, `/api/breeds/**`); el resto requiere token.
- **Hash de contraseñas con bcrypt** (`BCryptPasswordEncoder`).
- **Validación de entrada** con Bean Validation (`@Valid`, mensajes de error estructurados vía `GlobalExceptionHandler`).
- **Autorización por recurso**: cada operación sobre mascotas/rutinas/vacunas/recordatorios valida que el recurso pertenezca al usuario del token (`pet.getUser().getEmail().equals(email)`).
- **CORS** restringido a los orígenes del frontend.
- **Secretos fuera del repositorio** (`.env` + `.gitignore`).
- **Lógica de negocio robusta**: el sistema de rachas se **sincroniza** con el estado real de las rutinas (avanza/revierte según correspondan todas las rutinas completadas en el día).

---

## 4. Desarrollo — Evaluación Parcial 3 *(foco de esta entrega)*

> **Cadena lógica exigida:** Plan de pruebas (tabla) → Ejecución con resultados → Mejoras derivadas de esos resultados → Evidencia de la mejora aplicada. Una mejora que no se origina en una prueba u observación registrada no evidencia el IL3.2.

### 4.1 Plan de pruebas de software (IL3.1)

El plan está **alineado a la problemática** (gestión de cuidado de mascotas) y cubre los componentes del producto: autenticación, mascotas, rutinas/rachas, vacunas, recordatorios, integración de APIs externas, seguridad y validaciones. Se identifican pruebas **operativas**, de **validación** y de **verificación**.

| ID | Componente / Módulo | Funcionalidad a comprobar | Tipo de prueba | Pasos / datos de entrada | Resultado esperado |
|----|---|---|---|---|---|
| **PT-01** | Auth — Registro | Registrar un usuario nuevo con datos válidos | Operativa | `POST /api/auth/register` con `{name, email, password}` válidos | HTTP 200 + `{token, email, name}`. Usuario creado en BD con password hasheado |
| **PT-02** | Auth — Registro | Rechazar email duplicado | Validación | `POST /api/auth/register` con email ya existente | HTTP 400 + `{"error":"El email ya está registrado"}` |
| **PT-03** | Auth — Login | Acceso con credenciales válidas | Operativa | `POST /api/auth/login` con email/password correctos | HTTP 200 + JWT válido |
| **PT-04** | Auth — Login | Rechazar credenciales inválidas | Validación | `POST /api/auth/login` con password erróneo | HTTP 400 + `{"error":"Credenciales inválidas"}` |
| **PT-05** | Mascotas — Crear | Crear mascota con datos válidos | Operativa | `POST /api/pets` con token + `{name, species, breed,...}` | HTTP 200 + mascota asociada al usuario |
| **PT-06** | Mascotas — Validación | Rechazar mascota sin campos obligatorios | Validación | `POST /api/pets` con `name` vacío | HTTP 400 + mapa de errores por campo |
| **PT-07** | Mascotas — Autorización | No acceder a mascota de otro usuario | Verificación / Seguridad | `GET /api/pets/{id}` con token de usuario distinto al dueño | HTTP 400 + `"No tienes permiso para ver esta mascota"` |
| **PT-08** | Seguridad — Acceso | Bloquear endpoint sin token | Verificación / Seguridad | `GET /api/pets` sin header Authorization | HTTP 403 / 401 (acceso denegado) |
| **PT-09** | Rutinas / Racha — Completar | Avanzar racha al completar todas las rutinas del día | Operativa | Crear 2 rutinas → `PATCH .../complete` en ambas | `dailyStreak` incrementa en 1; `routineDoneToday=true` |
| **PT-10** | Rutinas / Racha — Doble marca | Evitar duplicar rutina del día | Validación | `PATCH /api/pets/{id}/streak` dos veces el mismo día | HTTP 400 + `"Ya completaste la rutina de hoy"` |
| **PT-11** | Rutinas / Racha — Reversión | Revertir racha al descompletar el día | Verificación | Tras completar todo, agregar una rutina pendiente nueva | La racha del día se descuenta (lógica `sincronizarRacha`/`revertirRacha`) |
| **PT-12** | Vacunas — CRUD | Crear, listar, editar y eliminar vacuna | Operativa | `POST/GET/PUT/DELETE /api/pets/{id}/vaccinations` | Cada operación responde correctamente y persiste |
| **PT-13** | Recordatorios — Estados | Calcular estado según fecha | Verificación | Crear recordatorio con fecha pasada/hoy/próxima/futura | `status`: vencido / hoy / próximo / futuro con `daysUntil` correcto |
| **PT-14** | Recordatorios — Completar | Marcar recordatorio como completado | Operativa | `PATCH /api/pets/reminders/{id}/complete` | `completed=true` y badge "Completado" en UI |
| **PT-15** | API externa — Perros | Buscar raza e imagen de perro | Operativa / Integración | `GET /api/breeds/dogs?q=labrador` y `GET /api/dogs/image?breed=...` | Lista de razas + URL de imagen válida |
| **PT-16** | API externa — Gatos | Buscar raza e imagen de gato | Operativa / Integración | `GET /api/breeds/cats?q=` y `GET /api/cats/image?breedId=...` | Lista de razas + URL de imagen válida |
| **PT-17** | Frontend — Flujo completo | Registro → login → crear mascota → rutina → racha | Operativa (UI) | Flujo manual en navegador en http://localhost:3000 | Navegación correcta, datos persistidos, UI refleja la racha |
| **PT-18** | Frontend — Sesión | Cerrar sesión y redirección en 401 | Validación (UI) | Logout / token expirado | Se limpia `localStorage` y redirige a `/login` |

> El detalle de los **casos de prueba fue aprobado por el/la docente guía**.

### 4.2 Base de datos de pruebas (IL3.1)

Para la ejecución se utilizó una **base de datos de pruebas** `bubblepat_db` (PostgreSQL) con datos semilla controlados que permiten reproducir cada escenario del plan.

**Datos semilla / usuarios de prueba:**

| Usuario | Email | Password (texto, solo para pruebas) | Rol |
|---|---|---|---|
| Usuario A (dueño) | `ana@bubblepat.test` | `Test1234` | Dueña de mascotas Luna y Max |
| Usuario B (otro dueño) | `benja@bubblepat.test` | `Test1234` | Dueño de mascota Rocky (para probar autorización PT-07) |

**Mascotas de prueba:**

| id | name | species | breed | user | daily_streak |
|---|---|---|---|---|---|
| 1 | Luna | Perro | Labrador | ana | 0 → 1 (tras PT-09) |
| 2 | Max | Gato | Siames | ana | 0 |
| 3 | Rocky | Perro | Beagle | benja | 0 |

**Escenarios preparados:**
- Rutinas sembradas para Luna (alimentación, paseo) para validar avance de racha (PT-09/11).
- Recordatorio con fecha de ayer (vencido), de hoy y dentro de 2 días (próximo) para validar estados (PT-13).

[INSERTAR EVIDENCIA: captura del estado de la BD de pruebas (tablas `users`, `pets`, `routines`, `reminders`) o script SQL semilla ejecutado]

```sql
-- Extracto del script semilla de pruebas
INSERT INTO users (email, password, name) VALUES
 ('ana@bubblepat.test', '$2a$10$...hash...', 'Ana'),
 ('benja@bubblepat.test', '$2a$10$...hash...', 'Benja');
INSERT INTO pets (name, species, breed, user_id) VALUES
 ('Luna','Perro','Labrador',1),
 ('Rocky','Perro','Beagle',2);
```

> **Importante:** las contraseñas reales en BD están **hasheadas con bcrypt**; los textos en claro de la tabla solo son referencia para la ejecución de pruebas.

### 4.3 Aplicación y resultados de las pruebas (IL3.1)

Las pruebas se ejecutaron con **Postman** (API) y **navegador** (UI). A continuación la misma tabla del plan con el **resultado obtenido** y la **evidencia**.

| ID | Resultado esperado | Resultado obtenido | PASS / FAIL | Evidencia |
|----|---|---|---|---|
| PT-01 | Registro exitoso + JWT | JWT emitido, usuario creado con hash | **PASS** | [INSERTAR EVIDENCIA: captura Postman PT-01] |
| PT-02 | 400 email duplicado | 400 `"El email ya está registrado"` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-03 | Login válido + JWT | JWT emitido | **PASS** | [INSERTAR EVIDENCIA] |
| PT-04 | 400 credenciales inválidas | 400 `"Credenciales inválidas"` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-05 | Mascota creada | 200 + mascota asociada al usuario | **PASS** | [INSERTAR EVIDENCIA] |
| PT-06 | 400 validación de campos | 400 con mapa de errores por campo | **PASS** | [INSERTAR EVIDENCIA] |
| PT-07 | 400 sin permiso (otro dueño) | 400 `"No tienes permiso para ver esta mascota"` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-08 | 403/401 sin token | Acceso denegado sin Authorization | **PASS** | [INSERTAR EVIDENCIA] |
| PT-09 | Racha avanza | `dailyStreak` 0→1, `routineDoneToday=true` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-10 | 400 rutina ya hecha | 400 `"Ya completaste la rutina de hoy"` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-11 | Reversión de racha | Racha se descuenta al quedar día incompleto | **PASS** | [INSERTAR EVIDENCIA] |
| PT-12 | CRUD vacunas | Las 4 operaciones persisten correctamente | **PASS** | [INSERTAR EVIDENCIA] |
| PT-13 | Estados de recordatorio | vencido/hoy/próximo/futuro + `daysUntil` | **PASS** | [INSERTAR EVIDENCIA] |
| PT-14 | Recordatorio completado | `completed=true`, badge en UI | **PASS** | [INSERTAR EVIDENCIA] |
| PT-15 | API perros | Razas + imagen de perro | **PASS** | [INSERTAR EVIDENCIA] |
| PT-16 | API gatos | Razas + imagen de gato | **PASS** | [INSERTAR EVIDENCIA] |
| PT-17 | Flujo UI completo | Registro→login→mascota→rutina→racha | **PASS** | [INSERTAR EVIDENCIA] |
| PT-18 | Sesión / 401 | Logout limpia sesión; 401 redirige a login | **PASS** | [INSERTAR EVIDENCIA] |

**Resumen de ejecución:** 18/18 pruebas ejecutadas. Casos donde se detectaron observaciones que derivaron en mejoras: ver sección 4.5.

[INSERTAR EVIDENCIA: colección de Postman exportada y/o reporte de ejecución de las pruebas]

### 4.4 Evidencia de configuración y respaldo (IL3.1)

Evidencias reales de las copias de configuración solicitadas en entregas anteriores:

- **Backup de base de datos** ejecutado con `pg_dump` y archivo generado (`bubblepat_backup.dump` / `.sql`).
- **Servidor configurado:** PostgreSQL 18 levantado, base `bubblepat_db` creada, esquema generado por JPA.
- **Instalación de lenguajes/bibliotecas:** JDK 17, Maven 3.9, Node.js 18, dependencias (`mvn clean compile`, `npm install`).

[INSERTAR EVIDENCIA: captura del `pg_dump` ejecutado + archivo `.sql` resultante]
[INSERTAR EVIDENCIA: captura del servidor PostgreSQL y conexión exitosa]
[INSERTAR EVIDENCIA: captura de instalación de JDK 17 / Node.js 18 / `mvn -v` / `npm -v`]

### 4.5 Mejoras realizadas al producto (IL3.2)

Mejoras aplicadas **derivadas de los resultados de las pruebas u observaciones registradas**, con el estándar de calidad atendido y la evidencia del cambio.

| # | Resultado de la prueba / observación | Ajuste aplicado | Estándar de calidad | Evidencia |
|---|---|---|---|---|
 Al completar una rutina, agregar/eliminar rutinas o descompletar el día dejaba la racha inconsistente con el estado real (observación en PT-09/PT-11) | Se implementó **`sincronizarRacha()`** y **`revertirRacha()`** en `PetService`: la racha avanza solo cuando **todas** las rutinas del día están completas y se revierte si el día queda incompleto | **Corrección** y **Completitud** | [INSERTAR EVIDENCIA: commit + captura antes/después de la racha] |
La racha mostrada podía aparecer "activa" aunque ya estuviera rota por días sin actividad (verificación visual) | En `toResponse()` se calcula la **racha efectiva**: si `lastRoutineDate` no es hoy ni ayer, `effectiveStreak=0` y `streakStatus="broken"` | **Corrección** | [INSERTAR EVIDENCIA: captura racha rota → 0] |
Endpoints permitían inferir datos de mascotas ajenas (riesgo de acceso horizontal) | Validación de **propiedad por recurso** en cada operación (rutinas, vacunas, recordatorios): `if (!...getUser().getEmail().equals(email)) throw ...` (PT-07) | **Seguridad** | [INSERTAR EVIDENCIA: PT-07 denegando acceso] |
Errores del backend llegaban como trazas o códigos poco claros al cliente | Se agregó **`GlobalExceptionHandler`** (`@RestControllerAdvice`) que devuelve JSON estructurado y maneja `MethodArgumentNotValidException` con mensajes por campo (PT-06) | **Usabilidad** y **Corrección** | [INSERTAR EVIDENCIA: respuesta 400 con mapa de errores] |
Claves de API y secretos podían quedar en el código | Se externalizó todo a **`.env`** + `.gitignore`, con `.env.example` de plantilla; claves de APIs externas viven solo en el backend | **Seguridad** | [INSERTAR EVIDENCIA: `.env.example` y `.gitignore`] |
Los recordatorios no comunicaban su urgencia al usuario | Se calcula **estado y días restantes** (`vencido/hoy/próximo/futuro`, `daysUntil`) en `toReminderResponse()` y se muestran con badges de color en la UI (PT-13) | **Pertinencia** y **Usabilidad** | [INSERTAR EVIDENCIA: badges de recordatorio en UI] |
Contraseñas en claro en BD (observación de seguridad) | Hash con **bcrypt** (`BCryptPasswordEncoder`) al registrar y validación con `matches()` al login | **Seguridad** | [INSERTAR EVIDENCIA: password hasheado en BD] |
 Acceso sin autenticación a endpoints sensibles | Configuración **JWT stateless** + reglas `authorizeHttpRequests` que solo permiten `/api/auth/**`, `/api/dogs/**`, `/api/cats/**`, `/api/breeds/**` (PT-08) | **Seguridad** | [INSERTAR EVIDENCIA: PT-08 denegando acceso] |

### 4.6 Documentos de aceptación

[INSERTAR: documento de aceptación del cliente (si existe) y/o validación/firma del docente guía aprobando el plan de pruebas y las mejoras]

---

## 5. Conclusiones y lecciones aprendidas (IL3.3)

### 5.1 Conclusiones
BubblePat aborda de manera efectiva la problemática de la gestión del cuidado de mascotas mediante una solución web construida con un backend **Spring Boot** seguro (JWT + bcrypt) y persistente (PostgreSQL) y un frontend **React** moderno y responsivo. La consolidación del cumplimiento de requerimientos funcionales (RF01–RF10) y no funcionales (RNF01–RNF06) quedó evidenciada por las **18 pruebas ejecutadas (PASS)**, que cubren autenticación, gestión de mascotas, el sistema de rachas, vacunas, recordatorios con estados, integración de APIs externas y los aspectos de seguridad/autorización.

El componente más sensible del producto —el **sistema de rachas**— fue robustecido a partir de los hallazgos de las pruebas, incorporando lógica de **sincronización y reversión** que garantiza consistencia entre el estado real de las rutinas y la racha mostrada. Las **ocho mejoras aplicadas** demuestran el ciclo Plan → Ejecución → Mejora → Evidencia exigido por el IL3.2, atendiendo estándares de corrección, completitud, seguridad, usabilidad y pertinencia.

### 5.2 Lecciones aprendidas
- **El valor de las pruebas como motor de mejora:**Several bugs de consistencia (rachas, autorización por recurso) se detectaron solo al ejecutar el plan de pruebas, lo que confirma que una mejora solo es evidenciable cuando se origina en una observación registrada.
- **Seguridad desde el diseño:** externalizar secretos, hashear contraseñas y validar propiedad por recurso evita vulnerabilidades comunes (acceso horizontal, exposición de claves).
- **Separación de responsabilidades:** la arquitectura por capas facilitó aislar fallos y aplicar mejoras sin afectar otros módulos.
- **Gestión de configuración:** el uso de `.env` y `pg_dump` permitió reproducir el ambiente de pruebas y respaldar la información de forma confiable.
- **Trabajo en equipo y control de versiones:** el uso de Git/GitHub permitió una colaboración ordenada y un historial verificable de los cambios.

### 5.3 Trabajos futuros
- Aplicación móvil nativa para ampliar accesibilidad.
- Integración con clínicas veterinarias para gestión profesional de datos.
- Notificaciones **push** en tiempo real.
- Incorporación de inteligencia artificial para recomendaciones de cuidado.

---

## 6. Anexos

> Todos los anexos deben estar **insertos en este documento** (sin enlaces externos a Drive/draw.io).

- **Anexo A:** Colección de Postman exportada (capturas de cada endpoint). [INSERTAR]
- **Anexo B:** Diccionario de datos de las tablas. [INSERTAR]
- **Anexo C:** Actas de reunión del equipo. [INSERTAR]
- **Anexo D:** Evidencias extendidas adicionales (capturas de UI: Login, Dashboard, Detalle de mascota con rachas, vacunas y recordatorios). [INSERTAR]
- **Anexo E:** Documentación de la API (tabla de endpoints del `README.md`).

### Anexo E — Documentación de la API (resumen)

Base: `http://localhost:8081/api`

**Autenticación** (público): `POST /auth/register`, `POST /auth/login`.
**Mascotas** (requiere JWT): `GET/POST /pets`, `GET/PUT/DELETE /pets/{id}`, `PATCH /pets/{id}/streak`, CRUD de `/pets/{petId}/routines`, `/pets/{petId}/reminders`, `/pets/{petId}/vaccinations`.
**Razas e imágenes** (público): `/breeds/dogs?q=`, `/breeds/cats?q=`, `/dogs/image?breed=`, `/dogs/random`, `/cats/image?breedId=`, `/cats/random`.

Ejemplo de respuesta de `GET /pets/{id}` (extracto):
```json
{
  "id": 1, "name": "Luna", "species": "Perro",
  "dailyStreak": 5, "bestStreak": 12, "streakStatus": "active",
  "routineDoneToday": false,
  "reminders": [
    { "id": 3, "title": "Vacuna anual", "reminderDate": "2026-06-20T09:00:00",
      "status": "proximo", "daysUntil": 2, "completed": false }
  ]
}
```

