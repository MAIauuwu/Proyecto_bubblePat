import api from './client';

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
