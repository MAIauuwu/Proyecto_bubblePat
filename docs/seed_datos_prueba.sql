-- =====================================================================
-- BubblePat — Base de datos de PRUEBAS (semilla)
-- Informe Estado de Avance 3 — Sección 4.2 (IL3.1)
-- PostgreSQL 18 · base: bubblepat_db
-- ---------------------------------------------------------------------
-- Contraseñas en BD están HASHEADAS con bcrypt (BCryptPasswordEncoder).
-- Los textos en claro (Test1234) solo sirven para ejecutar las pruebas.
-- =====================================================================

-- Limpiar datos de prueba previos (opcional, para reproducibilidad)
DELETE FROM reminders;
DELETE FROM vaccinations;
DELETE FROM routines;
DELETE FROM pets;
DELETE FROM users;

-- Restablecer secuencias (JPA usa identity, ajustar si corresponde)
SELECT setval(pg_get_serial_sequence('users','id'), coalesce(max(id),1)) FROM users;
SELECT setval(pg_get_serial_sequence('pets','id'), coalesce(max(id),1)) FROM pets;

-- ===================== USUARIOS DE PRUEBA =====================
INSERT INTO users (name, email, password, created_at) VALUES
 ('Ana',   'ana@bubblepat.test',   '$2a$10$CbEd1uC14FEqNM4JSMpi7Owk9xRaYfNYpSPGcMKd2W0BifIhQmAO6', now()),  -- Test1234
 ('Benja', 'benja@bubblepat.test', '$2a$10$bu8C3OVS2KaXYA2ORxt9tu3OYrhDEzVLsS3bXp63xzWh6QHng9YXm', now());  -- Test1234

-- ===================== MASCOTAS DE PRUEBA =====================
-- Ana (id=1): dueña de Luna y Max   ·   Benja (id=2): dueño de Rocky
INSERT INTO pets (name, species, breed, user_id, daily_streak, best_streak, last_routine_date) VALUES
 ('Luna',  'Perro', 'Labrador', 1, 0, 0, NULL),
 ('Max',   'Gato',  'Siames',   1, 0, 0, NULL),
 ('Rocky', 'Perro', 'Beagle',   2, 0, 0, NULL);

-- ===================== RUTINAS (Luna) =====================
INSERT INTO routines (pet_id, type, description, completed, completed_at) VALUES
 (1, 'Alimentacion', 'Comer 2 veces al dia', false, NULL),
 (1, 'Paseo',        'Paseo de 30 min',      false, NULL);

-- ===================== RECORDATORIOS (estados: vencido/hoy/proximo) =====================
INSERT INTO reminders (pet_id, title, description, reminder_date, completed) VALUES
 (1, 'Vacuna vencida', 'Se paso de fecha',      (now() - interval '1 day')::timestamp, false),  -- vencido
 (1, 'Baño hoy',       'Hoy toca',              now()::timestamp,                       false),  -- hoy
 (1, 'Control vet',    'En 2 dias',             (now() + interval '2 days')::timestamp, false);  -- proximo

-- ===================== VACUNA (Luna) =====================
INSERT INTO vaccinations (pet_id, name, applied_date, next_dose_date, vet_name, notes) VALUES
 (1, 'Rabia', (now())::date, (now() + interval '1 year')::date, 'Dr. House', '1ra dosis');
