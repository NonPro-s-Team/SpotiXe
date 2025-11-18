import axios from "axios";
import { API_CONFIG, ENDPOINTS } from "../../config/api";

/**
 * Axios instance for Album API
 */
const albumApi = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: API_CONFIG.TIMEOUT,
});

/**
 * Request interceptor - Add auth token if available
 */
albumApi.interceptors.request.use(
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
albumApi.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message =
      error.response?.data?.message || error.message || "An error occurred";
    console.error("API Error:", message);
    return Promise.reject(new Error(message));
  }
);

/**
 * Get all albums
 * @returns {Promise<Array>} Array of album objects
 */
export const getAllAlbums = async () => {
  const response = await albumApi.get(ENDPOINTS.ALBUMS);
  return response;
};

/**
 * Get album by ID
 * @param {number} id - Album ID
 * @returns {Promise<Object>} Album object
 */
export const getAlbumById = async (id) => {
  const response = await albumApi.get(ENDPOINTS.ALBUM_BY_ID(id));
  return response;
};

/**
 * Create new album
 * @param {Object} albumData - Album data
 * @returns {Promise<Object>} Created album object
 */
export const createAlbum = async (albumData) => {
  const response = await albumApi.post(ENDPOINTS.ALBUMS, albumData);
  return response;
};

/**
 * Update album
 * @param {number} id - Album ID
 * @param {Object} albumData - Updated album data
 * @returns {Promise<Object>} Updated album object
 */
export const updateAlbum = async (id, albumData) => {
  const response = await albumApi.put(ENDPOINTS.ALBUM_BY_ID(id), albumData);
  return response;
};

/**
 * Delete album
 * @param {number} id - Album ID
 * @returns {Promise<Object>} Delete response
 */
export const deleteAlbum = async (id) => {
  const response = await albumApi.delete(ENDPOINTS.ALBUM_BY_ID(id));
  return response;
};

export default albumApi;
