import { useState, useRef, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import api from '../api/client';
import { getAllDogBreeds, getAllCatBreeds } from '../api/breeds';
import { isPremium } from '../api/plans';
import { useAuth } from '../context/AuthContext';

export default function PetForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);
  const { user } = useAuth();
  const premium = isPremium(user?.plan);
  const [limitReached, setLimitReached] = useState(false);

  const [form, setForm] = useState({
    name: '', species: '', breed: '', birthDate: '',
    weight: '', allergicTo: '', lastDeworming: '',
    sex: '', lastHeatDate: ''
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [breedSuggestions, setBreedSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [allBreeds, setAllBreeds] = useState([]);
  const suggestionsRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(e) {
      if (suggestionsRef.current && !suggestionsRef.current.contains(e.target)) {
        setShowSuggestions(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (isEditing) {
      loadPet();
    } else if (!premium) {
      api.get('/pets').then(({ data }) => {
        if (data.length >= 2) setLimitReached(true);
      }).catch(() => {});
    }
  }, [id]);

  const loadPet = async () => {
    try {
      const { data } = await api.get(`/pets/${id}`);
      setForm({
        name: data.name || '',
        species: data.species || '',
        breed: data.breed || '',
        birthDate: data.birthDate || '',
        weight: data.weight || '',
        allergicTo: data.allergicTo || '',
        lastDeworming: data.lastDeworming || '',
        sex: data.sex || '',
        lastHeatDate: data.lastHeatDate || ''
      });
      loadBreedsFor(data.species);
    } catch (err) {
      setError('Error al cargar mascota');
      navigate('/app');
    }
  };

  const loadBreedsFor = async (species) => {
    setAllBreeds([]);
    if (species === 'Perro') setAllBreeds(await getAllDogBreeds());
    else if (species === 'Gato') setAllBreeds(await getAllCatBreeds());
  };

  const COMMON_BREEDS = {
    Perro: ['Quiltro', 'Sin raza'],
    Gato: ['Sin raza'],
  };

  const handleBreedSearch = (value) => {
    setForm({ ...form, breed: value });
    if (!value) {
      setBreedSuggestions([]);
      setShowSuggestions(false);
      return;
    }
    const pool = [...(COMMON_BREEDS[form.species] || []), ...allBreeds];
    const filtered = pool
      .filter((b) => b.toLowerCase().includes(value.toLowerCase()))
      .slice(0, 50);
    setBreedSuggestions(filtered);
    setShowSuggestions(filtered.length > 0);
  };

  const selectBreed = (breed) => {
    setForm({ ...form, breed });
    setShowSuggestions(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (submitting) return; // evita doble envío (duplicados)
    setError('');
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        weight: form.weight ? parseFloat(form.weight) : null,
        birthDate: form.birthDate || null,
        lastDeworming: form.lastDeworming || null,
        sex: form.sex || null,
        lastHeatDate: form.lastHeatDate || null,
      };
      if (isEditing) {
        await api.put(`/pets/${id}`, payload);
      } else {
        await api.post('/pets', payload);
      }
      navigate('/app');
    } catch (err) {
      setError(err.response?.data?.error || 'Error al guardar mascota');
      setSubmitting(false);
    }
  };

  const showBreedAutocomplete = form.species === 'Perro' || form.species === 'Gato';

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-50 via-rose-50 to-purple-50 py-8">
      <div className="max-w-xl mx-auto px-4">
        <h2 className="text-2xl font-bold text-rose-500 mb-6">
          {isEditing ? 'Editar Mascota' : 'Agregar Mascota'}
        </h2>

        {error && <div className="bg-rose-50 text-rose-500 p-3 rounded-lg mb-4 border border-rose-100">{error}</div>}

        {limitReached && (
          <div className="bg-gradient-to-r from-rose-100 to-purple-100 text-rose-500 p-5 rounded-xl mb-4 border border-rose-200">
            <p className="font-bold text-lg">🐾 Has alcanzado el límite del plan gratuito</p>
            <p className="text-sm text-rose-400 mt-1">El plan gratuito permite hasta 2 mascotas. Mejora a Premium para registrar mascotas ilimitadas.</p>
            <Link to="/subscription" className="inline-block mt-3 bg-rose-400 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-rose-500 transition">
              Mejorar a Premium
            </Link>
          </div>
        )}

        {!limitReached && (
        <form onSubmit={handleSubmit} className="bg-white/70 backdrop-blur-sm p-6 rounded-xl shadow-sm border border-pink-100 space-y-4">
          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Nombre *</label>
            <input name="name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Especie *</label>
            <select name="species" value={form.species} onChange={(e) => {
              setForm({ ...form, species: e.target.value, breed: '' });
              setBreedSuggestions([]);
              setShowSuggestions(false);
              loadBreedsFor(e.target.value);
            }} required
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50">
              <option value="">Seleccionar...</option>
              <option value="Perro">Perro</option>
              <option value="Gato">Gato</option>
              <option value="Ave">Ave</option>
              <option value="Conejo">Conejo</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Sexo</label>
            <div className="flex gap-3">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="radio" name="sex" value="Hembra" checked={form.sex === 'Hembra'}
                  onChange={(e) => setForm({ ...form, sex: e.target.value })}
                  className="accent-rose-400" />
                <span className="text-sm text-rose-400">♀ Hembra</span>
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="radio" name="sex" value="Macho" checked={form.sex === 'Macho'}
                  onChange={(e) => setForm({ ...form, sex: e.target.value })}
                  className="accent-rose-400" />
                <span className="text-sm text-rose-400">♂ Macho</span>
              </label>
            </div>
          </div>

          {form.sex === 'Hembra' && ['Perro', 'Gato', 'Conejo', 'Ave'].includes(form.species) && (
            <div>
              <label className="block text-sm font-medium text-rose-400 mb-1">Fecha del último celo <span className="text-xs text-rose-200">(opcional)</span></label>
              <input type="date" name="lastHeatDate" max={new Date().toISOString().split('T')[0]} value={form.lastHeatDate}
                onChange={(e) => setForm({ ...form, lastHeatDate: e.target.value })}
                className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
              <p className="text-xs text-rose-200 mt-1">Para estimar el próximo celo según la especie.</p>
            </div>
          )}

          <div className="relative" ref={suggestionsRef}>
            <label className="block text-sm font-medium text-rose-400 mb-1">Raza</label>
            <input name="breed" value={form.breed} onChange={(e) => handleBreedSearch(e.target.value)}
              placeholder={showBreedAutocomplete ? 'Escribe para buscar razas...' : 'Raza'}
              autoComplete="off"
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
            {showBreedAutocomplete && (
              <span className="absolute right-3 top-9 text-xs text-rose-200">
                {form.species === 'Perro' ? '🐕' : '🐈'} Autocompletado
              </span>
            )}
            {showBreedAutocomplete && COMMON_BREEDS[form.species] && (
              <div className="flex gap-2 mt-1.5 flex-wrap">
                {COMMON_BREEDS[form.species].map((b) => (
                  <button type="button" key={b} onClick={() => selectBreed(b)}
                    className="text-xs bg-rose-50 text-rose-400 px-2.5 py-1 rounded-full border border-pink-100 hover:bg-rose-100 transition">
                    {b}
                  </button>
                ))}
              </div>
            )}
            {showSuggestions && breedSuggestions.length > 0 && (
              <ul className="absolute z-10 w-full bg-white border border-pink-100 rounded-lg mt-1 max-h-48 overflow-y-auto shadow-lg">
                {breedSuggestions.map((breed) => (
                  <li key={breed}
                    onClick={() => selectBreed(breed)}
                    className="px-4 py-2 hover:bg-rose-50 cursor-pointer text-sm text-rose-400">
                    {breed}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Fecha de nacimiento</label>
            <input type="date" name="birthDate" max={new Date().toISOString().split('T')[0]} value={form.birthDate} onChange={(e) => setForm({ ...form, birthDate: e.target.value })}
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Peso (kg)</label>
            <input type="number" step="0.1" name="weight" value={form.weight} onChange={(e) => setForm({ ...form, weight: e.target.value })}
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Alérgico a</label>
            <input name="allergicTo" value={form.allergicTo} onChange={(e) => setForm({ ...form, allergicTo: e.target.value })}
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
          </div>

          <div>
            <label className="block text-sm font-medium text-rose-400 mb-1">Última desparasitación</label>
            <input type="date" name="lastDeworming" value={form.lastDeworming} onChange={(e) => setForm({ ...form, lastDeworming: e.target.value })}
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50" />
          </div>

          <div className="flex gap-3 pt-4">
            <button type="button" onClick={() => navigate('/app')} disabled={submitting}
              className="flex-1 border border-pink-200 text-rose-300 py-2 rounded-lg hover:bg-pink-50 transition font-medium disabled:opacity-50">
              Cancelar
            </button>
            <button type="submit" disabled={submitting}
              className="flex-1 bg-rose-300 text-white py-2 rounded-lg hover:bg-rose-400 transition font-medium shadow-sm disabled:opacity-60">
              {submitting ? 'Guardando...' : (isEditing ? 'Guardar Cambios' : 'Guardar')}
            </button>
          </div>
        </form>
        )}
      </div>
    </div>
  );
}
