# BubblePat — Frontend

Interfaz web de **BubblePat**, la app de gestión de cuidado de mascotas. Permite registrar mascotas, llevar rutinas diarias con **rachas**, agendar **recordatorios** con estados de urgencia y administrar la ficha médica (vacunas).

> ℹ️ La documentación principal del proyecto (descripción general, arquitectura, base de datos y API) está en el repositorio del backend: [`Proyecto_bubblePat`](https://github.com/MAIauuwu/Proyecto_bubblePat).

## Tecnologías utilizadas

- **React 19** + **Vite 8**
- **Tailwind CSS 4**
- **React Router 7**
- **Axios** (con proxy hacia el backend)

## Requisitos previos

- Node.js 18+
- El backend de BubblePat corriendo en `http://localhost:8081` (ver repositorio backend)

## Instalación

```bash
git clone https://github.com/MAIauuwu/bubblepat_frontend.git
cd bubblepat_frontend
npm install
```

## Configuración

En desarrollo **no** se requiere configuración: Vite hace proxy de `/api` hacia `http://localhost:8081` (ver `vite.config.js`).

Para producción puedes definir `VITE_API_BASE_URL` en un archivo `.env` (ver `.env.example`). No se manejan secretos en el frontend: las claves de API viven en el backend.

## Uso / Ejecución

```bash
npm run dev      # Servidor de desarrollo (http://localhost:3000)
npm run build    # Build de producción a /dist
npm run preview  # Previsualiza el build
npm run lint     # ESLint
```

## Estructura

```
src/
├── api/          # Cliente axios + helpers de razas (proxy al backend)
├── context/      # AuthContext (login/registro/JWT)
├── pages/        # Login, Register, Dashboard, PetForm, PetDetail
├── assets/       # Logo e imágenes
├── App.jsx       # Rutas
└── main.jsx      # Punto de entrada
```

## Autores

- **Maura Ramírez Navarro** — `mau.ramirezn@duocuc.cl` — Frontend y backend, organización de tareas.
- **Allison Sepúlveda** — `all.sepulveda@duocuc.cl` — Documentación, informes técnicos y levantamiento de requisitos.
