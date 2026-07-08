import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import api from '../api/client';
import logo from '../assets/BubblePat.png';
import { getDogImageByBreed, getCatImageByBreed, getRandomDogImage, getRandomCatImage, getGenericSpeciesImage, SPECIES_EMOJI, SPECIES_GRADIENT } from '../api/breeds';

const statCard = 'bg-white/70 backdrop-blur-sm rounded-xl border border-pink-100 p-4 text-center shadow-sm';

export default function Dashboard() {
  const { user, logout } = useAuth();
  const [pets, setPets] = useState([]);
  const [petImages, setPetImages] = useState({});

  useEffect(() => {
    loadPets();
  }, []);

  const loadPets = async () => {
    try {
      const { data } = await api.get('/pets');
      setPets(data);
      loadImages(data);
    } catch (err) {
      console.error('Error cargando mascotas:', err);
    }
  };

  const loadImages = async (petsList) => {
    const images = {};
    await Promise.all(petsList.map(async (pet) => {
      try {
        let img = null;
        if (pet.species === 'Perro') {
          img = pet.breed ? await getDogImageByBreed(pet.breed) : null;
          if (!img) img = await getRandomDogImage();
        } else if (pet.species === 'Gato') {
          img = pet.breed ? await getCatImageByBreed(pet.breed) : null;
          if (!img) img = await getRandomCatImage();
        } else {
          img = getGenericSpeciesImage(pet.species, pet.id);
        }
        images[pet.id] = img;
      } catch {}
    }));
    setPetImages(images);
  };

  const handleStreak = async (petId) => {
    try {
      await api.patch(`/pets/${petId}/streak`);
      loadPets();
    } catch (err) {
      alert(err.response?.data?.error || 'Error al actualizar racha');
    }
  };

  // === Métricas globales del panel ===
  const totalPets = pets.length;
  const pendingToday = pets.reduce((acc, p) => acc + (p.reminders || []).filter(
    (r) => !r.completed && ['vencido', 'hoy', 'proximo'].includes(r.status)).length, 0);
  const avgWellness = totalPets ? Math.round(pets.reduce((acc, p) => acc + (p.wellness?.score || 0), 0) / totalPets) : 0;
  const activeStreaks = pets.filter((p) => (p.dailyStreak || 0) > 0).length;

  const wellnessColor = (s) => s >= 85 ? 'text-emerald-600' : s >= 60 ? 'text-amber-600' : s >= 35 ? 'text-orange-500' : 'text-rose-500';
  const wellnessBar = (s) => s >= 85 ? 'bg-emerald-400' : s >= 60 ? 'bg-amber-400' : s >= 35 ? 'bg-orange-400' : 'bg-rose-400';

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
          <img src={logo} alt="BubblePat" className="h-10 sm:h-14" />
          <div className="flex items-center gap-2 sm:gap-4">
            <span className="text-rose-300 text-sm hidden sm:inline">Hola, {user?.name}</span>
            <Link to="/settings" className="text-rose-400 hover:text-rose-500 font-medium text-xs sm:text-sm" title="Configuración / Apariencia">Configuración</Link>
            <button onClick={logout} className="text-rose-400 hover:text-rose-500 font-medium text-xs sm:text-sm">
              Cerrar Sesión
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-4 py-6 sm:py-8">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h2 className="text-xl sm:text-2xl font-bold text-rose-500">Panel principal</h2>
            <p className="text-rose-300 text-xs sm:text-sm">Resumen del cuidado de tus mascotas</p>
          </div>
          <Link to="/pets/new"
            className="bg-rose-300 text-white px-4 sm:px-6 py-2 rounded-lg hover:bg-rose-400 transition font-medium shadow-sm text-sm">
            + Mascota
          </Link>
        </div>

        {/* Resumen global */}
        {totalPets > 0 && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-8">
            <div className={statCard}>
              <p className="text-2xl font-bold text-rose-500">{totalPets}</p>
              <p className="text-xs text-rose-300">Mascotas</p>
            </div>
            <div className={statCard}>
              <p className={`text-2xl font-bold ${wellnessColor(avgWellness)}`}>{avgWellness}%</p>
              <p className="text-xs text-rose-300">Bienestar promedio</p>
            </div>
            <div className={statCard}>
              <p className="text-2xl font-bold text-amber-500">🔥 {activeStreaks}</p>
              <p className="text-xs text-rose-300">Rachas activas</p>
            </div>
            <div className={statCard}>
              <p className={`text-2xl font-bold ${pendingToday > 0 ? 'text-rose-500' : 'text-emerald-500'}`}>{pendingToday}</p>
              <p className="text-xs text-rose-300">Recordatorios pendientes</p>
            </div>
          </div>
        )}

        {totalPets === 0 ? (
          <div className="text-center py-16">
            <p className="text-rose-300 text-lg mb-4">No tienes mascotas registradas</p>
            <Link to="/pets/new" className="text-rose-400 hover:underline text-lg">
              Agrega tu primera mascota
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {pets.map((pet) => {
              const score = pet.wellness?.score || 0;
              const pendientes = (pet.reminders || []).filter(
                (r) => !r.completed && ['vencido', 'hoy', 'proximo'].includes(r.status));
              const rutinasHoy = (pet.routines || []).filter((r) => r.appliesToday);
              const hechasHoy = rutinasHoy.filter((r) => r.doneToday).length;
              const earnedBadges = (pet.badges || []).filter((b) => b.earned).length;

              return (
                <div key={pet.id} className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 overflow-hidden hover:shadow-md hover:border-rose-200 transition">
                  {petImages[pet.id] ? (
                    <img src={petImages[pet.id]} alt={pet.name} className="w-full h-40 object-cover" />
                  ) : (
                    <div className={`w-full h-40 bg-gradient-to-br ${SPECIES_GRADIENT[pet.species] || SPECIES_GRADIENT.Otro} flex items-center justify-center`}>
                      <span className="text-6xl">{SPECIES_EMOJI[pet.species] || SPECIES_EMOJI.Otro}</span>
                    </div>
                  )}
                  <div className="p-5">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <Link to={`/pets/${pet.id}`} className="text-xl font-bold text-rose-500 hover:text-rose-600">
                          {pet.name}
                        </Link>
                        <p className="text-rose-300 text-sm">{pet.species} {pet.breed && `· ${pet.breed}`}</p>
                      </div>
                      <div className="text-right">
                        <div className="bg-amber-50 text-amber-500 px-3 py-1 rounded-full text-sm font-bold inline-block">
                          {pet.dailyStreak}
                        </div>
                        {pet.bestStreak > 0 && (
                          <p className="text-[11px] text-rose-300 mt-1">Récord {pet.bestStreak}</p>
                        )}
                      </div>
                    </div>

                    {/* Bienestar */}
                    <div className="mb-3">
                      <div className="flex justify-between text-xs mb-1">
                        <span className="text-rose-300">Bienestar</span>
                        <span className={`font-bold ${wellnessColor(score)}`}>{score}% · {pet.wellness?.level || '—'}</span>
                      </div>
                      <div className="h-2 rounded-full bg-pink-100 overflow-hidden">
                        <div className={`h-full rounded-full ${wellnessBar(score)}`} style={{ width: `${score}%` }} />
                      </div>
                    </div>

                    {/* Resumen rápido */}
                    <div className="flex flex-wrap gap-2 text-[11px] mb-3">
                      {rutinasHoy.length > 0 && (
                        <span className={`px-2 py-1 rounded-full font-medium ${
                          hechasHoy === rutinasHoy.length ? 'bg-emerald-100 text-emerald-600' : 'bg-rose-100 text-rose-500'}`}>
                          {hechasHoy}/{rutinasHoy.length} rutinas hoy
                        </span>
                      )}
                      {pendientes.length > 0 && (
                        <span className="px-2 py-1 rounded-full bg-amber-100 text-amber-600 font-medium">
                          {pendientes.length} recordatorio{pendientes.length === 1 ? '' : 's'}
                        </span>
                      )}
                      <span className="px-2 py-1 rounded-full bg-purple-100 text-purple-500 font-medium">
                        {earnedBadges}/{pet.badges?.length || 0} logros
                      </span>
                    </div>

                    {pet.insights?.[0] && (
                      <div className="text-[11px] mb-3 px-2.5 py-1.5 rounded-lg bg-pink-50 border border-pink-100 text-rose-500 leading-snug">
                        {pet.insights[0].message}
                      </div>
                    )}

                    {pet.weight && <p className="text-rose-200 text-sm mb-3">Peso: {pet.weight} kg</p>}

                    <div className="flex gap-2">
                      <button
                        onClick={() => handleStreak(pet.id)}
                        disabled={pet.routineDoneToday || rutinasHoy.length === 0}
                        className={`flex-1 py-2 rounded-lg transition text-sm font-medium ${
                          pet.routineDoneToday || rutinasHoy.length === 0
                            ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                            : 'bg-emerald-200 text-emerald-700 hover:bg-emerald-300'
                        }`}>
                        {pet.routineDoneToday ? 'Hecho hoy' : 'Completar Rutina'}
                      </button>
                      <Link to={`/pets/${pet.id}`}
                        className="flex-1 bg-rose-100 text-rose-500 py-2 rounded-lg hover:bg-rose-200 transition text-sm font-medium text-center">
                        Ver Detalle
                      </Link>
                    </div>
                  </div>
                </div>
              );
            })}
           </div>
         )}
      </div>
    </div>
  );
}
