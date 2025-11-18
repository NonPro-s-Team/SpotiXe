/**
 * API Configuration
 * Centralized API endpoints and configuration
 */

/**
 * API Base URLs
 */
export const API_CONFIG = {
  // Main API endpoint
  BASE_URL: import.meta.env.VITE_API_BASE_URL,

  // Alternative endpoints (if needed)
  // AUTH_URL: import.meta.env.VITE_AUTH_URL || 'https://auth.spotixe.io.vn',
  // MEDIA_URL: import.meta.env.VITE_MEDIA_URL || 'https://cdn.spotixe.io.vn',

  // API timeout
  TIMEOUT: 10000, // 10 seconds

  // Retry configuration
  RETRY_ATTEMPTS: 2,
  RETRY_DELAY: 1000, // 1 second
};

/**
 * API Endpoints
 */
export const ENDPOINTS = {
  // Songs
  SONGS: "/songs",
  SONG_BY_ID: (id) => `/songs/${id}`,
  SONG_COVER: (id) => `/songs/${id}/cover`,
  SONG_AUDIO: (id) => `/songs/${id}/audio`,

  // Artists
  ARTISTS: "/artists",
  ARTIST_BY_ID: (id) => `/artists/${id}`,

  // Albums
  ALBUMS: "/albums",
  ALBUM_BY_ID: (id) => `/albums/${id}`,

  // Users
  USERS: "/users",
  USER_BY_ID: (id) => `/users/${id}`,

  // Playlists
  PLAYLISTS: "/playlists",
  PLAYLIST_BY_ID: (id) => `/playlists/${id}`,

  // Analytics
  ANALYTICS: "/analytics",
  ANALYTICS_OVERVIEW: "/analytics/overview",
  ANALYTICS_TRENDS: "/analytics/trends",
};

/**
 * File upload limits
 */
export const UPLOAD_LIMITS = {
  // Image files
  IMAGE_MAX_SIZE: 5 * 1024 * 1024, // 5MB
  IMAGE_ACCEPTED_TYPES: ["image/jpeg", "image/jpg", "image/png", "image/webp"],

  // Audio files
  AUDIO_MAX_SIZE: 50 * 1024 * 1024, // 50MB
  AUDIO_ACCEPTED_TYPES: ["audio/mpeg", "audio/mp3", "audio/wav", "audio/flac"],
};

export default API_CONFIG;
