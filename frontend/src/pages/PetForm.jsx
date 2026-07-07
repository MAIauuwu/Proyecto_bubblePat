import { useState, useRef, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/client';
import { searchDogBreeds, searchCatBreeds } from '../api/breeds';

export default function PetForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);

  const [form, setForm] = useState({
    name: '', species: '', breed: '', birthDate: '',
    weight: '', allergicTo: '', lastDeworming: ''
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [breedSuggestions, setBreedSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const suggestionsRef = useRef(null);
  const debounceRef = useRef(null);

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
        lastDeworming: data.lastDeworming || ''
      });
    } catch (err) {
      setError('Error al cargar mascota');
      navigate('/');
    }
  };

  const handleBreedSearch = (value) => {
    setForm({ ...form, breed: value });
    if (debounceRef.current) clearTimeout(debounceRef.current);
    if (!value || !form.species) {
      setBreedSuggestions([]);
      setShowSuggestions(false);
      return;
    }
    debounceRef.current = setTimeout(async () => {
      try {
        let results = [];
        if (form.species === 'Perro') {
          results = await searchDogBreeds(value);
        } else if (form.species === 'Gato') {
          results = await searchCatBreeds(value);
        }
        setBreedSuggestions(results.map((r) => r.name));
        setShowSuggestions(results.length > 0);
      } catch {
        setBreedSuggestions([]);
      }
    }, 300);
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
      };
      if (isEditing) {
        await api.put(`/pets/${id}`, payload);
      } else {
        await api.post('/pets', payload);
      }
      navigate('/');
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
            }} required
              className="w-full px-4 py-2 border border-pink-100 rounded-lg focus:ring-2 focus:ring-rose-300 outline-none bg-pink-50/50">
              <option value="">Seleccionar...</option>
              <option value="Perro">Perro</option>
              <option value="Gato">Gato</option>
              <option value="Ave">Ave</option>
              <option value="Conejo">Conejo</option>
              <option value="Pez">Pez</option>
            </select>
          </div>

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
            <input type="date" name="birthDate" value={form.birthDate} onChange={(e) => setForm({ ...form, birthDate: e.target.value })}
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
            <button type="button" onClick={() => navigate('/')} disabled={submitting}
              className="flex-1 border border-pink-200 text-rose-300 py-2 rounded-lg hover:bg-pink-50 transition font-medium disabled:opacity-50">
              Cancelar
            </button>
            <button type="submit" disabled={submitting}
              className="flex-1 bg-rose-300 text-white py-2 rounded-lg hover:bg-rose-400 transition font-medium shadow-sm disabled:opacity-60">
              {submitting ? 'Guardando...' : (isEditing ? 'Guardar Cambios' : 'Guardar')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
