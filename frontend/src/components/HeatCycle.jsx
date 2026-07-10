import { useState } from 'react';

const HEAT = {
  Perro:  { interval: 182, duration: 21, firstMonths: 8,  note: 'Celo cada ~6 meses · dura ~3 semanas · sangrado visible' },
  Gato:   { interval: 21,  duration: 7,  firstMonths: 5,  note: 'Poliéstrica estacional · cada 2–3 sem en primavera/verano · sin sangrado' },
  Conejo: { interval: 0,   duration: 0,  firstMonths: 5,  note: 'Ovulación inducida · receptiva casi siempre · sin ciclo fijo' },
  Ave:    { interval: 0,   duration: 0,  firstMonths: 6,  note: 'Temporada de cría estacional · depende de la especie' },
};

const today = () => new Date();
const addDays = (iso, n) => { const d = new Date(iso); d.setDate(d.getDate() + n); return d.toISOString().split('T')[0]; };
const daysUntil = (iso) => Math.ceil((new Date(iso) - today()) / (86400000));
const ageYears = (iso) => iso ? (today() - new Date(iso)) / (365.25 * 86400000) : null;
const fmt = (iso) => iso ? new Date(iso + 'T00:00:00').toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';

export default function HeatCycle({ pet, onSave }) {
  const [editing, setEditing] = useState(false);
  const [date, setDate] = useState(pet.lastHeatDate || '');

  const info = HEAT[pet.species];
  if (!info || pet.sex !== 'Hembra') return null;

  const age = ageYears(pet.birthDate);
  const canHeat = age == null || age * 12 >= info.firstMonths;
  const hasCycle = info.interval > 0;

  const next = pet.lastHeatDate ? addDays(pet.lastHeatDate, info.interval) : null;
  const days = next ? daysUntil(next) : null;
  const inHeat = next ? days <= info.duration && days >= -info.duration : false;
  const overdue = days != null && days < -info.duration;

  const estPrimero = age != null && age * 12 < info.firstMonths;

  return (
    <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-5">
      <div className="flex items-center justify-between mb-3">
        <h2 className="text-lg font-bold text-rose-500">Ciclo de celo ♀</h2>
        <span className="text-[11px] px-2 py-0.5 rounded-full bg-pink-50 text-rose-400 border border-pink-100">{pet.species}</span>
      </div>

      <p className="text-xs text-rose-300 mb-3">🌸 {info.note}</p>

      {!canHeat && (
        <p className="text-sm text-amber-600 bg-amber-50 border border-amber-200 rounded-lg px-3 py-2 mb-3">
          Aún no tiene edad para el primer celo (estimado: ~{info.firstMonths} meses{pet.birthDate ? `, tiene ${Math.floor(age * 12)} meses` : ''}).
        </p>
      )}

      {/* Especies sin ciclo fijo (conejo/ave) */}
      {!hasCycle && (
        <div className="bg-purple-50 border border-purple-100 rounded-lg px-3 py-2 text-sm text-purple-600">
          {pet.species === 'Conejo'
            ? '🐰 Las conejas no tienen celo periódico: ovulan al aparearse. Pueden quedar preñadas en cualquier momento.'
            : '🐦 Las aves dependen de la temporada de cría (generalmente primavera). Observa conductas de anidación.'}
        </div>
      )}

      {/* Especies con ciclo calculable (perro/gato) */}
      {hasCycle && canHeat && (
        <div className="space-y-3">
          {pet.lastHeatDate ? (
            <>
              <div className="grid grid-cols-2 gap-3">
                <div className="bg-pink-50/60 rounded-lg p-3 border border-pink-100">
                  <p className="text-xs text-rose-300">Último celo</p>
                  <p className="text-sm font-bold text-rose-500">{fmt(pet.lastHeatDate)}</p>
                </div>
                <div className="bg-pink-50/60 rounded-lg p-3 border border-pink-100">
                  <p className="text-xs text-rose-300">Próximo estimado</p>
                  <p className="text-sm font-bold text-rose-500">{fmt(next)}</p>
                </div>
              </div>

              <div className={`rounded-lg p-3 border-l-4 ${
                inHeat ? 'bg-rose-50 border-rose-400'
                : overdue ? 'bg-amber-50 border-amber-400'
                : days <= 14 ? 'bg-yellow-50 border-yellow-400'
                : 'bg-emerald-50 border-emerald-400'
              }`}>
                {inHeat && <p className="text-sm font-bold text-rose-600">🔴 Posiblemente en celo ahora</p>}
                {!inHeat && overdue && <p className="text-sm font-bold text-amber-600">⏰ Celo atrasado — el próximo podría estar cerca</p>}
                {!inHeat && !overdue && days <= 14 && <p className="text-sm font-bold text-yellow-700">⏳ Próximo celo en ~{days} días ({fmt(next)})</p>}
                {!inHeat && !overdue && days > 14 && <p className="text-sm font-bold text-emerald-600">✅ Faltan ~{days} días para el próximo celo</p>}
              </div>
            </>
          ) : (
            <p className="text-sm text-rose-300 bg-pink-50/60 rounded-lg px-3 py-2 border border-pink-100">
              Registra la fecha del último celo para estimar el próximo.
              {estPrimero
                ? ` Aún le falta para el primero (~${info.firstMonths} meses).`
                : ''}
            </p>
          )}

          {/* Editor de fecha */}
          {editing ? (
            <form onSubmit={(e) => { e.preventDefault(); setEditing(false); onSave(date); }} className="flex gap-2">
              <input type="date" value={date} max={today().toISOString().split('T')[0]}
                onChange={(e) => setDate(e.target.value)}
                className="flex-1 px-3 py-2 border border-pink-100 rounded-lg text-sm bg-pink-50/50 focus:ring-2 focus:ring-rose-300 outline-none" />
              <button type="submit" className="bg-rose-300 text-white px-4 py-2 rounded-lg text-sm hover:bg-rose-400 transition">Guardar</button>
              <button type="button" onClick={() => { setEditing(false); setDate(pet.lastHeatDate || ''); }}
                className="bg-gray-100 text-gray-500 px-3 py-2 rounded-lg text-sm hover:bg-gray-200 transition">Cancelar</button>
            </form>
          ) : (
            <button onClick={() => setEditing(true)}
              className="w-full text-left px-3 py-2 rounded-lg bg-white/70 border border-pink-100 text-rose-400 text-sm font-medium hover:bg-rose-50 transition">
              {pet.lastHeatDate ? '✎ Actualizar fecha de celo' : '+ Registrar último celo'}
            </button>
          )}
        </div>
      )}
    </div>
  );
}
