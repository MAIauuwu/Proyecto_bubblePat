const CYCLES = {
  Perro:  { stages: [['Cachorro','🍼','0–1 año','Vacunas y socialización'],['Adolescente','🐕','1–2 años','Energía alta, entrenamiento'],['Adulto','🦴','2–7 años','Vigor pleno, ejercicio'],['Senior','🩺','7+ años','Chequeos y dieta suave']], life: '10–13 años' },
  Gato:   { stages: [['Gatito','🐱','0–1 año','Crecimiento y juego'],['Joven','😼','1–3 años','Activo, marca territorio'],['Adulto','🐈','3–10 años','Tranquilo, control de peso'],['Senior','🩺','10+ años','Control renal y dental']], life: '12–18 años' },
  Conejo: { stages: [['Gazapo','🐰','0–6 meses','Destete progresivo'],['Joven','🐇','6 m–1 año','Castración recomendada'],['Adulto','🥕','1–5 años','Heno y ejercicio'],['Senior','🩺','5+ años','Articulaciones y dientes']], life: '8–12 años' },
  Ave:    { stages: [['Polluelo','🥚','0–1 mes','En el nido'],['Juvenil','🐣','1–6 meses','Primeras plumas'],['Adulto','🐦','6 m–10 años','Reproducción y canto'],['Senior','🩺','10+ años','Plumaje reducido']], life: 'Según especie' },
  Pez:    { stages: [['Alevín','🐟','0–2 meses','Agua muy estable'],['Joven','🐠','2–6 meses','Desarrollo de color'],['Adulto','🫧','6 m–2 años','Reproducción posible'],['Senior','🩺','2+ años','Agua crítica']], life: '1–30 años' },
};

const yearsFrom = (iso) => {
  if (!iso) return null;
  const d = new Date(iso);
  if (isNaN(d)) return null;
  return (Date.now() - d.getTime()) / (365.25 * 24 * 3600 * 1000);
};

const stageIndex = (stages, age) => {
  if (age == null) return -1;
  if (age < 1) return 0;
  if (age < 2) return stages.length > 2 ? 1 : 0;
  return Math.min(2, stages.length - 2);
};

export default function AnimalCycle({ species, birthDate }) {
  const c = CYCLES[species];
  if (!c) return null;
  const age = yearsFrom(birthDate);
  const active = stageIndex(c.stages, age);

  return (
    <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-5">
      <div className="flex items-center justify-between mb-3">
        <h2 className="text-lg font-bold text-rose-500">Ciclo de vida · {species}</h2>
        <span className="text-xs text-rose-300">Esperanza: {c.life}</span>
      </div>

      {age != null && <p className="text-xs text-rose-300 mb-3">Edad actual: {age.toFixed(1)} años</p>}

      <div className="flex items-center gap-1 overflow-x-auto pb-1">
        {c.stages.map(([name, emoji, range, desc], i) => (
          <div key={name} className="flex items-center">
            <div className={`flex flex-col items-center text-center px-3 py-2 rounded-xl border transition min-w-[90px] ${
              i === active
                ? 'bg-gradient-to-br from-rose-100 to-amber-50 border-rose-300 ring-2 ring-rose-200'
                : i < active ? 'bg-pink-50/50 border-pink-100 opacity-60' : 'bg-white/50 border-pink-100'
            }`}>
              <span className="text-2xl">{emoji}</span>
              <span className="text-xs font-bold text-rose-500 mt-1">{name}</span>
              <span className="text-[10px] text-rose-300">{range}</span>
              <span className="text-[10px] text-rose-200 mt-0.5">{desc}</span>
              {i === active && <span className="text-[10px] text-rose-500 font-medium mt-1">● Etapa actual</span>}
            </div>
            {i < c.stages.length - 1 && <span className="text-rose-200 text-sm px-0.5">→</span>}
          </div>
        ))}
      </div>
    </div>
  );
}
