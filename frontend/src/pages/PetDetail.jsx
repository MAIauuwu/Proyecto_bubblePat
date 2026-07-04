import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/client';
import { getDogImageByBreed, getCatImageByBreed } from '../api/breeds';
import logo from '../assets/BubblePat.png';

const statusColor = { ok: 'bg-emerald-400', warning: 'bg-amber-400', bad: 'bg-rose-300' };
const statusWidth = { ok: '100%', warning: '55%', bad: '12%' };
const statusText = { ok: 'text-emerald-600', warning: 'text-amber-600', bad: 'text-rose-500' };

const timeAgo = (iso) => {
  if (!iso) return '';
  const d = new Date(iso);
  const secs = Math.floor((Date.now() - d.getTime()) / 1000);
  if (secs < 60) return 'recién';
  const mins = Math.floor(secs / 60);
  if (mins < 60) return `hace ${mins} min`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `hace ${hrs} h`;
  const days = Math.floor(hrs / 24);
  if (days === 1) return 'ayer';
  if (days < 7) return `hace ${days} días`;
  return d.toLocaleDateString('es-ES', { day: '2-digit', month: 'short' });
};

// Días de la semana (código 3 letras backend <-> etiqueta ES corta)
const DAYS = [
  { code: 'MON', label: 'L' }, { code: 'TUE', label: 'M' }, { code: 'WED', label: 'X' },
  { code: 'THU', label: 'J' }, { code: 'FRI', label: 'V' }, { code: 'SAT', label: 'S' }, { code: 'SUN', label: 'D' },
];
const DAY_ES = { MON: 'L', TUE: 'M', WED: 'X', THU: 'J', FRI: 'V', SAT: 'S', SUN: 'D' };
const formatSchedule = (r) => {
  const parts = [];
  if (r.startTime || r.endTime) parts.push(`🕑 ${r.startTime || '?'}–${r.endTime || '?'}`);
  if (r.daysOfWeek) parts.push(r.daysOfWeek.split(',').map((d) => DAY_ES[d] || d).join(' '));
  return parts.join(' · ');
};

export default function PetDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pet, setPet] = useState(null);
  const [activeTab, setActiveTab] = useState('routines');
  const [breedImage, setBreedImage] = useState(null);

  const [routineForm, setRoutineForm] = useState({ type: '', description: '', startTime: '', endTime: '', days: [] });
  const emptyRoutineForm = { type: '', description: '', startTime: '', endTime: '', days: [] };
  const [editingRoutine, setEditingRoutine] = useState(null);
  const [vaccineForm, setVaccineForm] = useState({ name: '', appliedDate: '', nextDoseDate: '', vetName: '', notes: '' });
  const [editingVaccine, setEditingVaccine] = useState(null);
  const [reminderForm, setReminderForm] = useState({ title: '', description: '', reminderDate: '', time: '' });
  const [editingReminder, setEditingReminder] = useState(null);

  useEffect(() => {
    loadPet();
  }, [id]);

  const loadPet = async () => {
    try {
      const { data } = await api.get(`/pets/${id}`);
      setPet(data);
      if (data.breed) {
        let img = null;
        if (data.species === 'Perro') {
          img = await getDogImageByBreed(data.breed);
        } else if (data.species === 'Gato') {
          img = await getCatImageByBreed(data.breed);
        }
        setBreedImage(img);
      }
    } catch (err) {
      alert('Error al cargar mascota');
      navigate('/');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('¿Eliminar esta mascota?')) {
      await api.delete(`/pets/${id}`);
      navigate('/');
    }
  };

  const addRoutine = async (e) => {
    e.preventDefault();
    const payload = {
      type: routineForm.type,
      description: routineForm.description || null,
      startTime: routineForm.startTime || null,
      endTime: routineForm.endTime || null,
      daysOfWeek: routineForm.days && routineForm.days.length ? routineForm.days.join(',') : null,
    };
    if (editingRoutine) {
      await api.put(`/pets/routines/${editingRoutine}`, payload);
      setEditingRoutine(null);
    } else {
      await api.post(`/pets/${id}/routines`, payload);
    }
    setRoutineForm(emptyRoutineForm);
    loadPet();
  };

  const toggleDay = (code) => {
    setRoutineForm((f) => ({
      ...f,
      days: f.days.includes(code) ? f.days.filter((d) => d !== code) : [...f.days, code],
    }));
  };

  const startEditRoutine = (r) => {
    setEditingRoutine(r.id);
    setRoutineForm({
      type: r.type,
      description: r.description || '',
      startTime: r.startTime || '',
      endTime: r.endTime || '',
      days: r.daysOfWeek ? r.daysOfWeek.split(',') : [],
    });
  };

  const cancelEditRoutine = () => {
    setEditingRoutine(null);
    setRoutineForm(emptyRoutineForm);
  };

  const completeRoutine = async (routineId) => {
    await api.patch(`/pets/routines/${routineId}/complete`);
    loadPet();
  };

  const deleteRoutine = async (routineId) => {
    if (window.confirm('¿Eliminar esta rutina?')) {
      await api.delete(`/pets/routines/${routineId}`);
      loadPet();
    }
  };

  const addVaccination = async (e) => {
    e.preventDefault();
    if (editingVaccine) {
      await api.put(`/pets/vaccinations/${editingVaccine}`, vaccineForm);
      setEditingVaccine(null);
    } else {
      await api.post(`/pets/${id}/vaccinations`, vaccineForm);
    }
    setVaccineForm({ name: '', appliedDate: '', nextDoseDate: '', vetName: '', notes: '' });
    loadPet();
  };

  const startEditVaccine = (v) => {
    setEditingVaccine(v.id);
    setVaccineForm({
      name: v.name,
      appliedDate: v.appliedDate || '',
      nextDoseDate: v.nextDoseDate || '',
      vetName: v.vetName || '',
      notes: v.notes || ''
    });
  };

  const cancelEditVaccine = () => {
    setEditingVaccine(null);
    setVaccineForm({ name: '', appliedDate: '', nextDoseDate: '', vetName: '', notes: '' });
  };

  const deleteVaccination = async (vaccinationId) => {
    if (window.confirm('¿Eliminar esta vacuna?')) {
      await api.delete(`/pets/vaccinations/${vaccinationId}`);
      loadPet();
    }
  };

  const addReminder = async (e) => {
    e.preventDefault();
    const time = reminderForm.time || '09:00';
    const payload = {
      title: reminderForm.title,
      description: reminderForm.description || null,
      reminderDate: reminderForm.reminderDate ? `${reminderForm.reminderDate}T${time}:00` : null
    };
    if (editingReminder) {
      await api.put(`/pets/reminders/${editingReminder}`, payload);
      setEditingReminder(null);
    } else {
      await api.post(`/pets/${id}/reminders`, payload);
    }
    setReminderForm({ title: '', description: '', reminderDate: '', time: '' });
    loadPet();
  };

  const startEditReminder = (r) => {
    setEditingReminder(r.id);
    setReminderForm({
      title: r.title,
      description: r.description || '',
      reminderDate: r.reminderDate ? r.reminderDate.split('T')[0] : '',
      time: r.reminderDate ? (r.reminderDate.split('T')[1] || '').slice(0, 5) : ''
    });
  };

  const cancelEditReminder = () => {
    setEditingReminder(null);
    setReminderForm({ title: '', description: '', reminderDate: '', time: '' });
  };

  const completeReminder = async (reminderId) => {
    await api.patch(`/pets/reminders/${reminderId}/complete`);
    loadPet();
  };

  const deleteReminder = async (reminderId) => {
    if (window.confirm('¿Eliminar este recordatorio?')) {
      await api.delete(`/pets/reminders/${reminderId}`);
      loadPet();
    }
  };

  if (!pet) return <div className="min-h-screen flex items-center justify-center text-rose-300">Cargando...</div>;

  const streakInfo = {
    done_today: { label: '¡Rutina de hoy completada!', color: 'text-emerald-500' },
    active: { label: '¡Sigue con la racha, completa la rutina de hoy!', color: 'text-amber-600' },
    broken: { label: 'La racha se rompió, ¡empieza de nuevo!', color: 'text-rose-400' },
    never: { label: 'Aún no tienes racha', color: 'text-rose-300' },
  };
  const streak = streakInfo[pet.streakStatus] || streakInfo.never;

  const routinesToday = pet.routines || [];
  const doneTodayCount = routinesToday.filter((r) => r.doneToday).length;
  const totalRoutines = routinesToday.length;

  const wellness = pet.wellness || { score: 0, level: '—', items: [] };
  const scoreColor = wellness.score >= 85 ? 'text-emerald-600'
    : wellness.score >= 60 ? 'text-amber-600'
    : wellness.score >= 35 ? 'text-orange-500' : 'text-rose-500';

  const badges = pet.badges || [];
  const earnedBadges = badges.filter((b) => b.earned).length;

  // Calendario de racha (read-only): últimos días contados, sin opción de "marcar hacia atrás".
  const streakDays = (pet.streakDays || []).slice(-14);
  const fmtDay = (iso) => new Date(iso + 'T00:00:00').getDate();

  const formatDateTime = (iso) => {
    if (!iso) return null;
    const d = new Date(iso);
    if (isNaN(d)) return iso;
    const dateStr = d.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
    const hasTime = d.getHours() !== 0 || d.getMinutes() !== 0;
    return hasTime
      ? `${dateStr} · ${d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}`
      : dateStr;
  };

  const reminderBadge = (r) => {
    if (r.completed) return { text: 'Completado', cls: 'bg-emerald-100 text-emerald-600' };
    switch (r.status) {
      case 'vencido': return { text: 'Vencido', cls: 'bg-rose-100 text-rose-600' };
      case 'hoy': return { text: 'Hoy', cls: 'bg-amber-100 text-amber-700' };
      case 'proximo': return { text: 'Próximo', cls: 'bg-yellow-100 text-yellow-700' };
      case 'futuro': return { text: 'Programado', cls: 'bg-sky-100 text-sky-600' };
      default: return { text: 'Sin fecha', cls: 'bg-gray-100 text-gray-500' };
    }
  };

  const vaccineBadge = (v) => {
    switch (v.status) {
      case 'al_dia': return { text: 'Al día', cls: 'bg-emerald-100 text-emerald-600' };
      case 'por_vencer': return { text: 'Por vencer', cls: 'bg-amber-100 text-amber-700' };
      case 'vencida': return { text: 'Vencida', cls: 'bg-rose-100 text-rose-600' };
      default: return { text: 'Sin próxima', cls: 'bg-gray-100 text-gray-500' };
    }
  };

  const relativeText = (r) => {
    if (r.completed || r.reminderDate == null) return null;
    const d = r.daysUntil;
    if (d < 0) return `Venció hace ${Math.abs(d)} día${Math.abs(d) === 1 ? '' : 's'}`;
    if (d === 0) return 'Hoy';
    if (d === 1) return 'Mañana';
    return `En ${d} días`;
  };

  const sortedReminders = [...(pet.reminders || [])].sort((a, b) => {
    if (a.completed !== b.completed) return a.completed ? 1 : -1;
    const da = a.daysUntil ?? Number.MAX_SAFE_INTEGER;
    const db = b.daysUntil ?? Number.MAX_SAFE_INTEGER;
    return da - db;
  });

  const inputClass = 'px-3 py-2 border border-pink-100 rounded-lg text-sm bg-pink-50/50 focus:ring-2 focus:ring-rose-300 outline-none';
  const btnPrimary = 'bg-rose-300 text-white px-4 py-2 rounded-lg text-sm hover:bg-rose-400 transition shadow-sm';
  const btnSecondary = 'bg-gray-100 text-gray-500 px-3 py-1 rounded-lg text-xs hover:bg-gray-200 transition';
  const btnDanger = 'text-rose-400 hover:text-rose-500 text-xs';
  const cardClass = 'bg-white/70 backdrop-blur-sm p-4 rounded-xl border border-pink-100';

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <button onClick={() => navigate('/')}
              className="flex items-center gap-1 text-rose-400 hover:text-rose-500 font-medium text-sm transition"
              title="Volver al panel">
              <span className="text-lg">←</span>
              <span className="hidden sm:inline">Atrás</span>
            </button>
            <Link to="/" className="flex items-center gap-2">
              <img src={logo} alt="BubblePat" className="h-10" />
            </Link>
          </div>
          <div className="flex gap-4">
            <Link to={`/pets/${id}/edit`} className="text-rose-300 hover:text-rose-400 font-medium text-sm">
              Editar Mascota
            </Link>
            <button onClick={handleDelete} className="text-rose-400 hover:text-rose-500 font-medium text-sm">
              Eliminar Mascota
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
        {/* ===== Header + racha ===== */}
        <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-6">
          <div className="flex justify-between items-start">
            <div className="flex gap-4">
              {breedImage && (
                <img src={breedImage} alt={pet.breed} className="w-24 h-24 rounded-xl object-cover border border-pink-100" />
              )}
              <div>
                <h1 className="text-3xl font-bold text-rose-500">{pet.name}</h1>
                <p className="text-rose-300 mt-1">{pet.species} {pet.breed && `· ${pet.breed}`}</p>
                {pet.birthDate && <p className="text-rose-200 text-sm mt-1">Nacimiento: {pet.birthDate}</p>}
              </div>
            </div>
            <div className="text-center flex flex-col items-center gap-2 min-w-[180px]">
              <div className="bg-amber-50 text-amber-500 px-4 py-2 rounded-full">
                <span className="text-2xl font-bold">🔥 {pet.dailyStreak}</span>
                <p className="text-xs">días de racha</p>
              </div>
              {pet.bestStreak > 0 && (
                <p className="text-xs text-rose-300">🏆 Récord: {pet.bestStreak} días</p>
              )}
              <p className={`text-xs font-medium ${streak.color}`}>{streak.label}</p>
              {totalRoutines === 0 ? (
                <p className="text-xs text-rose-300">Agrega rutinas para alimentar la racha 🔥</p>
              ) : pet.routineDoneToday ? (
                <p className="text-xs text-emerald-600 font-medium">✓ {doneTodayCount}/{totalRoutines} rutinas hoy</p>
              ) : (
                <p className="text-xs text-rose-400 font-medium">
                  {doneTodayCount}/{totalRoutines} rutinas hoy · completa todas para sumar
                </p>
              )}
            </div>
          </div>

          {/* Calendario de racha (read-only) */}
          {streakDays.length > 0 && (
            <div className="mt-4 pt-4 border-t border-pink-100">
              <div className="flex items-center justify-between mb-2">
                <p className="text-xs text-rose-300 font-medium">
                  🗓️ Racha desde {pet.streakStartDate} <span className="text-rose-200">(automática)</span>
                </p>
                <p className="text-[11px] text-rose-200">No se puede modificar ni retroceder</p>
              </div>
              <div className="flex gap-1 flex-wrap">
                {streakDays.map((d, i) => (
                  <div key={i} title={d}
                    className="w-7 h-7 rounded-full bg-gradient-to-br from-rose-300 to-amber-300 text-white text-[11px] font-bold flex items-center justify-center shadow-sm">
                    {fmtDay(d)}
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="mt-4 pt-4 border-t border-pink-100 grid grid-cols-3 gap-4 text-sm">
            {pet.weight && <div><span className="text-rose-200">Peso:</span> <span className="font-medium text-rose-400">{pet.weight} kg</span></div>}
            {pet.allergicTo && <div><span className="text-rose-200">Alérgico a:</span> <span className="font-medium text-rose-400">{pet.allergicTo}</span></div>}
            {pet.lastDeworming && <div><span className="text-rose-200">Desparasitación:</span> <span className="font-medium text-rose-400">{pet.lastDeworming}</span></div>}
          </div>
        </div>

        {/* ===== Estado general (bienestar) ===== */}
        <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-rose-500">Estado general de {pet.name}</h2>
            <div className="text-right">
              <span className={`text-3xl font-bold ${scoreColor}`}>{wellness.score}%</span>
              <p className={`text-xs font-medium ${scoreColor}`}>{wellness.level}</p>
            </div>
          </div>
          {wellness.items.length === 0 ? (
            <p className="text-sm text-rose-300">Agrega rutinas y vacunas para calcular el estado de {pet.name}.</p>
          ) : (
            <div className="space-y-3">
              {wellness.items.map((it) => (
                <div key={it.key}>
                  <div className="flex justify-between items-center text-sm mb-1">
                    <span className="text-rose-400 font-medium">{it.icon} {it.label}</span>
                    <span className={`text-xs font-medium ${statusText[it.status] || 'text-gray-400'}`}>{it.detail}</span>
                  </div>
                  <div className="h-2.5 rounded-full bg-pink-100 overflow-hidden">
                    <div className={`h-full rounded-full transition-all ${statusColor[it.status] || 'bg-gray-300'}`}
                      style={{ width: statusWidth[it.status] || '12%' }} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* ===== Medallas ===== */}
        <div className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-rose-500">🏅 Logros</h2>
            <span className="text-xs text-rose-300 font-medium">{earnedBadges}/{badges.length} desbloqueadas</span>
          </div>
          <div className="grid grid-cols-3 sm:grid-cols-5 gap-3">
            {badges.map((b) => (
              <div key={b.key} title={b.description}
                className={`flex flex-col items-center text-center p-3 rounded-xl border transition ${
                  b.earned
                    ? 'bg-gradient-to-br from-amber-50 to-rose-50 border-amber-200'
                    : 'bg-gray-50 border-gray-100 opacity-50 grayscale'
                }`}>
                <span className="text-2xl">{b.earned ? b.emoji : '🔒'}</span>
                <span className={`text-[11px] mt-1 font-medium ${b.earned ? 'text-rose-400' : 'text-gray-400'}`}>{b.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* ===== Tabs ===== */}
        <div className="flex gap-2 flex-wrap">
          {['routines', 'vaccinations', 'reminders', 'activity'].map((tab) => (
            <button key={tab} onClick={() => setActiveTab(tab)}
              className={`px-4 py-2 rounded-lg font-medium text-sm transition ${
                activeTab === tab ? 'bg-rose-300 text-white shadow-sm' : 'bg-white/70 text-rose-300 border border-pink-100 hover:bg-rose-50'
              }`}>
              {tab === 'routines' ? 'Rutinas' : tab === 'vaccinations' ? 'Vacunas' : tab === 'reminders' ? 'Recordatorios' : 'Actividad'}
            </button>
          ))}
        </div>

        {activeTab === 'routines' && (
          <div className="space-y-4">
            <form onSubmit={addRoutine} className={cardClass}>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Tipo *</label>
                  <select value={routineForm.type} onChange={(e) => setRoutineForm({...routineForm, type: e.target.value})}
                    required className={inputClass + ' w-full'}>
                    <option value="">Seleccionar</option>
                    <option value="feeding">🍖 Alimentación</option>
                    <option value="walk">🦮 Paseo</option>
                    <option value="water">💧 Agua</option>
                    <option value="medicine">💊 Medicina</option>
                    <option value="bath">🛁 Baño</option>
                    <option value="other">Otro</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Descripción</label>
                  <input value={routineForm.description} onChange={(e) => setRoutineForm({...routineForm, description: e.target.value})}
                    className={inputClass + ' w-full'} />
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Hora inicio</label>
                  <input type="time" value={routineForm.startTime}
                    onChange={(e) => setRoutineForm({...routineForm, startTime: e.target.value})}
                    className={inputClass + ' w-full'} />
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Hora fin</label>
                  <input type="time" value={routineForm.endTime}
                    onChange={(e) => setRoutineForm({...routineForm, endTime: e.target.value})}
                    className={inputClass + ' w-full'} />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs text-rose-300 mb-1">Días (vacío = todos los días)</label>
                  <div className="flex gap-1 flex-wrap">
                    {DAYS.map((d) => (
                      <button type="button" key={d.code} onClick={() => toggleDay(d.code)}
                        className={`w-9 h-9 rounded-lg text-sm font-bold transition ${
                          routineForm.days.includes(d.code) ? 'bg-rose-300 text-white' : 'bg-pink-50 text-rose-300 border border-pink-100 hover:bg-rose-100'
                        }`}>{d.label}</button>
                    ))}
                  </div>
                </div>
              </div>
              <div className="flex gap-2 mt-3">
                <button type="submit" className={btnPrimary}>
                  {editingRoutine ? 'Guardar' : 'Agregar'}
                </button>
                {editingRoutine && (
                  <button type="button" onClick={cancelEditRoutine} className={btnSecondary}>Cancelar</button>
                )}
              </div>
            </form>

            {(!pet.routines || pet.routines.length === 0) && (
              <div className={`${cardClass} text-center text-rose-300 text-sm`}>
                No hay rutinas. Agrega las actividades diarias (alimentación, paseo, agua, medicina…) para alimentar la racha 🔥
              </div>
            )}

            {pet.routines?.map((r) => {
              const schedule = formatSchedule(r);
              return (
                <div key={r.id} className={`${cardClass} ${r.doneToday ? 'opacity-60' : ''}`}>
                  <div className="flex justify-between items-center gap-3">
                    <div className="min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="font-medium text-rose-400">{r.typeLabel}</span>
                        {!r.appliesToday && (
                          <span className="text-[10px] px-2 py-0.5 rounded-full bg-gray-100 text-gray-400">No aplica hoy</span>
                        )}
                        {r.doneToday && <span className="text-emerald-500 text-sm">✓ Hecha hoy</span>}
                      </div>
                      {r.description && <p className="text-rose-200 text-sm mt-0.5">{r.description}</p>}
                      {schedule && <p className="text-rose-300 text-xs mt-1">{schedule}</p>}
                    </div>
                    <div className="flex gap-2 items-center shrink-0">
                      {r.appliesToday && !r.doneToday && (
                        <button onClick={() => completeRoutine(r.id)}
                          className="bg-emerald-200 text-emerald-700 px-3 py-1 rounded-lg text-sm hover:bg-emerald-300 transition">
                          Completar
                        </button>
                      )}
                      <button onClick={() => startEditRoutine(r)} className={btnSecondary}>Editar</button>
                      <button onClick={() => deleteRoutine(r.id)} className={btnDanger}>Eliminar</button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {activeTab === 'vaccinations' && (
          <div className="space-y-4">
            <form onSubmit={addVaccination} className={cardClass}>
              <div className="grid grid-cols-2 gap-3">
                <input placeholder="Nombre de vacuna *" value={vaccineForm.name}
                  onChange={(e) => setVaccineForm({...vaccineForm, name: e.target.value})} required
                  className={inputClass} />
                <input type="date" value={vaccineForm.appliedDate}
                  onChange={(e) => setVaccineForm({...vaccineForm, appliedDate: e.target.value})}
                  className={inputClass} />
                <input type="date" value={vaccineForm.nextDoseDate}
                  onChange={(e) => setVaccineForm({...vaccineForm, nextDoseDate: e.target.value})}
                  className={inputClass} />
                <input placeholder="Nombre del veterinario" value={vaccineForm.vetName}
                  onChange={(e) => setVaccineForm({...vaccineForm, vetName: e.target.value})}
                  className={inputClass} />
                <input placeholder="Notas" value={vaccineForm.notes}
                  onChange={(e) => setVaccineForm({...vaccineForm, notes: e.target.value})}
                  className={inputClass + ' col-span-2'} />
              </div>
              <p className="text-[11px] text-rose-200 mt-2">💡 Si pones fecha de próxima dosis, se crea un recordatorio automático.</p>
              <div className="flex gap-2 mt-3">
                <button type="submit" className={btnPrimary}>
                  {editingVaccine ? 'Guardar' : 'Agregar Vacuna'}
                </button>
                {editingVaccine && (
                  <button type="button" onClick={cancelEditVaccine} className={btnSecondary}>Cancelar</button>
                )}
              </div>
            </form>

            {pet.vaccinations?.map((v) => {
              const vb = vaccineBadge(v);
              return (
                <div key={v.id} className={cardClass}>
                  <div className="flex justify-between items-start">
                    <div>
                      <div className="flex items-center gap-2 flex-wrap">
                        <h3 className="font-bold text-rose-400">{v.name}</h3>
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${vb.cls}`}>{vb.text}</span>
                      </div>
                      <div className="text-sm text-rose-300 mt-1">
                        {v.appliedDate && <span>Aplicada: {v.appliedDate}</span>}
                        {v.nextDoseDate && <span className="ml-4">Próxima dosis: {v.nextDoseDate}</span>}
                        {v.vetName && <span className="ml-4">Vet: {v.vetName}</span>}
                      </div>
                      {v.notes && <p className="text-rose-200 text-sm mt-1">{v.notes}</p>}
                    </div>
                    <div className="flex gap-2">
                      <button onClick={() => startEditVaccine(v)} className={btnSecondary}>Editar</button>
                      <button onClick={() => deleteVaccination(v.id)} className={btnDanger}>Eliminar</button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {activeTab === 'reminders' && (
          <div className="space-y-4">
            <form onSubmit={addReminder} className={cardClass}>
              <div className="grid grid-cols-2 gap-3">
                <div className="col-span-2">
                  <label className="block text-xs text-rose-300 mb-1">Título *</label>
                  <input value={reminderForm.title}
                    onChange={(e) => setReminderForm({...reminderForm, title: e.target.value})} required
                    className={inputClass + ' w-full'} />
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Fecha</label>
                  <input type="date" value={reminderForm.reminderDate}
                    onChange={(e) => setReminderForm({...reminderForm, reminderDate: e.target.value})}
                    className={inputClass + ' w-full'} />
                </div>
                <div>
                  <label className="block text-xs text-rose-300 mb-1">Hora (opcional)</label>
                  <input type="time" value={reminderForm.time || ''}
                    onChange={(e) => setReminderForm({...reminderForm, time: e.target.value})}
                    className={inputClass + ' w-full'} />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs text-rose-300 mb-1">Descripción</label>
                  <input value={reminderForm.description || ''}
                    onChange={(e) => setReminderForm({...reminderForm, description: e.target.value})}
                    placeholder="Detalle del recordatorio"
                    className={inputClass + ' w-full'} />
                </div>
              </div>
              <div className="flex gap-2 mt-3">
                <button type="submit" className={btnPrimary}>
                  {editingReminder ? 'Guardar' : 'Agregar'}
                </button>
                {editingReminder && (
                  <button type="button" onClick={cancelEditReminder} className={btnSecondary}>Cancelar</button>
                )}
              </div>
            </form>

            {sortedReminders.length === 0 && (
              <div className={`${cardClass} text-center text-rose-300 text-sm`}>
                No hay recordatorios. Agrega uno o registra una vacuna con próxima dosis.
              </div>
            )}

            {sortedReminders.map((r) => {
              const badge = reminderBadge(r);
              const rel = relativeText(r);
              return (
                <div key={r.id} className={`${cardClass} ${r.completed ? 'opacity-60' : ''}`}>
                  <div className="flex justify-between items-start gap-3">
                    <div className="min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <h3 className="font-bold text-rose-400">{r.title}</h3>
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${badge.cls}`}>
                          {badge.text}
                        </span>
                        {r.automatic && (
                          <span className="text-[10px] px-2 py-0.5 rounded-full bg-purple-100 text-purple-500 font-medium">
                            ⚙️ Automático
                          </span>
                        )}
                      </div>
                      {r.description && <p className="text-rose-300 text-sm mt-1">{r.description}</p>}
                      <p className="text-rose-200 text-xs mt-1">
                        {r.reminderDate ? formatDateTime(r.reminderDate) : 'Sin fecha'}
                        {rel && <span className="ml-2 font-medium text-rose-400">· {rel}</span>}
                      </p>
                    </div>
                    <div className="flex gap-2 items-center shrink-0">
                      {!r.completed && (
                        <>
                          <button onClick={() => startEditReminder(r)} className={btnSecondary}>Editar</button>
                          <button onClick={() => completeReminder(r.id)}
                            className="bg-emerald-200 text-emerald-700 px-3 py-1 rounded-lg text-xs hover:bg-emerald-300 transition">
                            Completar
                          </button>
                        </>
                      )}
                      <button onClick={() => deleteReminder(r.id)} className={btnDanger}>Eliminar</button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {activeTab === 'activity' && (() => {
          const agendaHoy = (pet.routines || [])
            .filter((r) => r.appliesToday)
            .sort((a, b) => (a.startTime || '99:99').localeCompare(b.startTime || '99:99'));
          return (
            <div className="space-y-6">
              <div className="space-y-3">
                <h3 className="text-sm font-bold text-rose-400">📋 Agenda de hoy</h3>
                {agendaHoy.length === 0 ? (
                  <div className={`${cardClass} text-center text-rose-300 text-sm`}>No hay rutinas programadas para hoy.</div>
                ) : agendaHoy.map((r) => (
                  <div key={r.id} className={`${cardClass} flex justify-between items-center gap-3 ${r.doneToday ? 'opacity-60' : ''}`}>
                    <div className="min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="text-xs font-mono text-rose-400 bg-pink-50 px-2 py-0.5 rounded">
                          {r.startTime ? `${r.startTime}${r.endTime ? `–${r.endTime}` : ''}` : '—'}
                        </span>
                        <span className="font-medium text-rose-400">{r.typeLabel}</span>
                        {r.doneToday && <span className="text-emerald-500 text-sm">✓</span>}
                      </div>
                      {r.description && <p className="text-rose-200 text-sm mt-0.5">{r.description}</p>}
                    </div>
                    <div className="flex gap-2 items-center shrink-0">
                      {!r.doneToday && (
                        <button onClick={() => completeRoutine(r.id)}
                          className="bg-emerald-200 text-emerald-700 px-3 py-1 rounded-lg text-xs hover:bg-emerald-300 transition">
                          Completar
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              <div className="space-y-3">
                <h3 className="text-sm font-bold text-rose-400">🕒 Actividad reciente</h3>
                {(!pet.activityLog || pet.activityLog.length === 0) ? (
                  <div className={`${cardClass} text-center text-rose-300 text-sm`}>Aún no hay actividad registrada para {pet.name}.</div>
                ) : pet.activityLog.map((a) => (
                  <div key={a.id} className={`${cardClass} flex items-center gap-3`}>
                    <span className="text-2xl">{a.icon || '•'}</span>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-rose-400">{a.title}</p>
                      <p className="text-[11px] text-rose-200">{timeAgo(a.createdAt)}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          );
        })()}
      </div>
    </div>
  );
}
