const SPECIES_CARE = {
  Perro:  { color: 'bg-rose-50 border-rose-200 text-rose-500', items: ['Paseo diario', 'Vacunas caninas', 'Desparasitación', 'Razas (The Dog API)'] },
  Gato:   { color: 'bg-purple-50 border-purple-200 text-purple-500', items: ['Vacunas felinas', 'Control de peso', 'Razas (The Cat API)'] },
  Conejo: { color: 'bg-emerald-50 border-emerald-200 text-emerald-600', items: ['Alimentación con heno', 'Limpieza del hábitat', 'Control dental'] },
  Ave:    { color: 'bg-sky-50 border-sky-200 text-sky-500', items: ['Cambio de agua', 'Limpieza de jaula', 'Control de plumaje', 'Alimentación'] },
};

const SPECIES_ICON = { Perro: '🐶', Gato: '🐱', Conejo: '🐰', Ave: '🐦' };

export default function SpeciesCare() {
  return (
    <div className="mt-10">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-bold text-rose-500">Personalización según especie</h2>
        <p className="text-rose-300 text-sm max-w-xl mx-auto">
          BubblePat no es solo un registro de mascotas: adapta sus recordatorios, rutinas e
          información al tipo de animal que tienes.
        </p>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {Object.entries(SPECIES_CARE).map(([species, info]) => (
          <div key={species} className={`rounded-xl border-2 p-4 ${info.color}`}>
            <div className="flex items-center gap-2 mb-3">
              <span className="text-2xl">{SPECIES_ICON[species]}</span>
              <h3 className="font-bold">{species}</h3>
            </div>
            <ul className="space-y-1.5 text-xs opacity-90">
              {info.items.map((it) => (
                <li key={it} className="flex items-start gap-1.5">
                  <span className="mt-0.5">•</span>{it}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </div>
  );
}
