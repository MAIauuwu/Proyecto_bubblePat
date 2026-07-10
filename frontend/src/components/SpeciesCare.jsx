const CARE = {
  Perro:  { items: ['Paseo diario', 'Vacunas caninas', 'Desparasitación', 'Cepillado de pelo', 'Corte de uñas', 'Higiene dental', 'Socialización', 'Control de garrapatas'] },
  Gato:   { items: ['Vacunas felinas', 'Control de peso', 'Arena sanitaria', 'Rascador', 'Juego diario', 'Cepillado', 'Higiene dental'] },
  Conejo: { items: ['Alimentación con heno', 'Limpieza del hábitat', 'Control dental', 'Ejercicio fuera de la jaula', 'Castración recomendada', 'Corte de uñas'] },
  Ave:    { items: ['Cambio de agua', 'Limpieza de jaula', 'Control de plumaje', 'Alimentación', 'Baño de sol', 'Juguetes y estimulación', 'Temperatura adecuada'] },
};

export default function SpeciesCare({ species }) {
  const info = CARE[species];
  if (!info) return null;
  return (
    <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-5">
      <h2 className="text-lg font-bold text-rose-500 mb-1">Cuidados para {species}</h2>
      <p className="text-xs text-rose-300 mb-3">BubblePat adapta sus funciones al tipo de animal que tienes.</p>
      <div className="flex flex-wrap gap-2">
        {info.items.map((it) => (
          <span key={it} className="px-3 py-1.5 rounded-full bg-pink-50 border border-pink-100 text-rose-500 text-xs font-medium">
            {it}
          </span>
        ))}
      </div>
    </div>
  );
}
