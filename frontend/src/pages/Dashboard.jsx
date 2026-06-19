import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import api from '../api/client';
import logo from '../assets/BubblePat.png';
import { getDogImageByBreed, getCatImageByBreed, getRandomDogImage, getRandomCatImage } from '../api/breeds';

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
        if (pet.species === 'Perro' && pet.breed) {
          images[pet.id] = await getDogImageByBreed(pet.breed);
        } else if (pet.species === 'Perro') {
          images[pet.id] = await getRandomDogImage();
        } else if (pet.species === 'Gato' && pet.breed) {
          images[pet.id] = await getCatImageByBreed(pet.breed);
        } else if (pet.species === 'Gato') {
          images[pet.id] = await getRandomCatImage();
        }
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

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50">
      <nav className="bg-white/60 backdrop-blur-sm border-b border-pink-100">
        <div className="max-w-6xl mx-auto px-4 py-3 flex justify-between items-center">
          <img src={logo} alt="BubblePat" className="h-10" />
          <div className="flex items-center gap-4">
            <span className="text-rose-300">Hola, {user?.name}</span>
            <button onClick={logout} className="text-rose-400 hover:text-rose-500 font-medium">
              Cerrar Sesión
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-bold text-rose-500">Mis Mascotas</h2>
          <Link
            to="/pets/new"
            className="bg-rose-300 text-white px-6 py-2 rounded-lg hover:bg-rose-400 transition font-medium shadow-sm"
          >
            + Agregar Mascota
          </Link>
        </div>

        {pets.length === 0 ? (
          <div className="text-center py-16">
            <p className="text-rose-300 text-lg mb-4">No tienes mascotas registradas</p>
            <Link to="/pets/new" className="text-rose-400 hover:underline text-lg">
              Agrega tu primera mascota
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {pets.map((pet) => (
              <div key={pet.id} className="bg-white/70 backdrop-blur-sm rounded-xl shadow-sm border border-pink-100 overflow-hidden hover:shadow-md hover:border-rose-200 transition">
                {petImages[pet.id] && (
                  <img src={petImages[pet.id]} alt={pet.name} className="w-full h-40 object-cover" />
                )}
                <div className="p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <Link to={`/pets/${pet.id}`} className="text-xl font-bold text-rose-500 hover:text-rose-600">
                        {pet.name}
                      </Link>
                      <p className="text-rose-300">{pet.species} {pet.breed && `· ${pet.breed}`}</p>
                    </div>
                    <div className="text-right">
                      <div className="bg-amber-50 text-amber-500 px-3 py-1 rounded-full text-sm font-bold inline-block">
                        🔥 {pet.dailyStreak}
                      </div>
                      {pet.bestStreak > 0 && (
                        <p className="text-[11px] text-rose-300 mt-1">🏆 Récord {pet.bestStreak}</p>
                      )}
                    </div>
                  </div>

                  {pet.weight && <p className="text-rose-200 text-sm">Peso: {pet.weight} kg</p>}

                  <div className="flex gap-2 mt-4">
                    <button
                      onClick={() => handleStreak(pet.id)}
                      disabled={pet.routineDoneToday}
                      className={`flex-1 py-2 rounded-lg transition text-sm font-medium ${
                        pet.routineDoneToday
                          ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                          : 'bg-emerald-200 text-emerald-700 hover:bg-emerald-300'
                      }`}
                    >
                      {pet.routineDoneToday ? '✓ Hecho hoy' : 'Completar Rutina'}
                    </button>
                    <Link
                      to={`/pets/${pet.id}`}
                      className="flex-1 bg-rose-100 text-rose-500 py-2 rounded-lg hover:bg-rose-200 transition text-sm font-medium text-center"
                    >
                      Ver Detalle
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
