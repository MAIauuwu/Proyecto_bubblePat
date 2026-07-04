import api from './client';

// Fallback visual por especie cuando no hay foto real (API caída o especie sin API)
export const SPECIES_EMOJI = {
  Perro: '🐶', Gato: '🐈', Ave: '🐦', Conejo: '🐰', Pez: '🐠', Otro: '🐾',
};
export const SPECIES_GRADIENT = {
  Perro: 'from-amber-200 to-orange-200',
  Gato: 'from-purple-200 to-pink-200',
  Ave: 'from-sky-200 to-cyan-200',
  Conejo: 'from-rose-200 to-pink-200',
  Pez: 'from-blue-200 to-teal-200',
  Otro: 'from-gray-200 to-rose-200',
};

export async function searchDogBreeds(query) {
  if (!query) return [];
  try {
    const { data } = await api.get('/breeds/dogs', { params: { q: query } });
    return data.map((name) => ({ name }));
  } catch {
    return [];
  }
}

export async function searchCatBreeds(query) {
  if (!query) return [];
  try {
    const { data } = await api.get('/breeds/cats', { params: { q: query } });
    return data.map((name) => ({ name }));
  } catch {
    return [];
  }
}

export async function getRandomDogImage() {
  try {
    const { data } = await api.get('/dogs/random');
    return data.imageUrl;
  } catch {
    return null;
  }
}

export async function getRandomCatImage() {
  try {
    const { data } = await api.get('/cats/random');
    return data.url;
  } catch {
    return null;
  }
}

export async function getDogImageByBreed(breed) {
  try {
    const { data } = await api.get('/dogs/image', { params: { breed } });
    return data.imageUrl;
  } catch {
    return null;
  }
}

export async function getCatImageByBreed(breedId) {
  try {
    const { data } = await api.get('/cats/image', { params: { breedId } });
    return data.url;
  } catch {
    return null;
  }
}
