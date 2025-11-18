import axios from "axios";
import { API_CONFIG, ENDPOINTS } from "../../config/api";

/**
 * Axios instance for Artist API
 */
const artistApi = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: API_CONFIG.TIMEOUT,
});

/**
 * Request interceptor - Add auth token if available
 */
artistApi.interceptors.request.use(
  (config) => {
    // Get token from Firebase auth if needed
    // const token = auth.currentUser?.accessToken;
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle errors globally
 */
artistApi.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message =
      error.response?.data?.message || error.message || "An error occurred";
    console.error("API Error:", message);
    return Promise.reject(new Error(message));
  }
);

/**
 * Get all artists
 * @returns {Promise<Array>} Array of artist objects
 */
export const getAllArtists = async () => {
  const response = await artistApi.get(ENDPOINTS.ARTISTS);
  return response;
};

/**
 * Get artist by ID
 * @param {number} id - Artist ID
 * @returns {Promise<Object>} Artist object
 */
export const getArtistById = async (id) => {
  const response = await artistApi.get(ENDPOINTS.ARTIST_BY_ID(id));
  return response;
};

/**
 * Create new artist
 * @param {Object} artistData - Artist data
 * @param {AbortSignal} signal - Optional abort signal
 * @returns {Promise<Object>} Created artist object
 */
export const createArtist = async (artistData, signal = null) => {
  const config = signal ? { signal } : {};
  const response = await artistApi.post(ENDPOINTS.ARTISTS, artistData, config);
  return response;
};

/**
 * Update artist
 * @param {number} id - Artist ID
 * @param {Object} artistData - Updated artist data
 * @returns {Promise<Object>} Updated artist object
 */
export const updateArtist = async (id, artistData) => {
  const response = await artistApi.put(ENDPOINTS.ARTIST_BY_ID(id), artistData);
  return response;
};

/**
 * Delete artist
 * @param {number} id - Artist ID
 * @returns {Promise<Object>} Delete response
 */
export const deleteArtist = async (id) => {
  const response = await artistApi.delete(ENDPOINTS.ARTIST_BY_ID(id));
  return response;
};

export default artistApi;
