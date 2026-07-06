import { createContext, useContext, useEffect, useState } from 'react';

const ThemeContext = createContext();

// Temas pastel predefinidos (cada uno es un "hue" de la rueda HSL).
export const PRESETS = [
  { name: 'Rosa', hue: 350, emoji: '🌸' },
  { name: 'Cielo', hue: 205, emoji: '🩵' },
  { name: 'Menta', hue: 155, emoji: '🌿' },
  { name: 'Lavanda', hue: 265, emoji: '💜' },
  { name: 'Melocotón', hue: 28, emoji: '🍑' },
  { name: 'Turquesa', hue: 180, emoji: '🐠' },
  { name: 'Arena', hue: 42, emoji: '🌼' },
  { name: 'Gris', hue: 220, emoji: '🪨' },
];

// Genera una paleta pastel y la inyecta en las variables de color de Tailwind v4
// (rose/pink/purple son los colores de marca; los semánticos como emerald/amber
// no se tocan). Así toda la app se recolorea sin cambiar cada componente.
function applyHue(hue) {
  const root = document.documentElement;
  const c = (s, l) => `hsl(${hue}, ${s}%, ${l}%)`;
  root.style.setProperty('--color-rose-500', c(72, 50));
  root.style.setProperty('--color-rose-400', c(68, 58));
  root.style.setProperty('--color-rose-300', c(64, 70));
  root.style.setProperty('--color-rose-200', c(58, 82));
  root.style.setProperty('--color-rose-50', c(70, 96));
  root.style.setProperty('--color-pink-50', c(80, 97));
  root.style.setProperty('--color-pink-100', c(72, 94));
  root.style.setProperty('--color-purple-50', `hsl(${(hue + 25) % 360}, 68%, 96%)`);
}

export function ThemeProvider({ children }) {
  const [hue, setHueState] = useState(() => {
    const saved = localStorage.getItem('bp_hue');
    return saved !== null ? Number(saved) : 350; // Rosa por defecto
  });

  useEffect(() => {
    applyHue(hue);
  }, [hue]);

  const setHue = (newHue) => {
    setHueState(newHue);
    localStorage.setItem('bp_hue', String(newHue));
  };

  return (
    <ThemeContext.Provider value={{ hue, setHue, presets: PRESETS }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  return useContext(ThemeContext);
}
