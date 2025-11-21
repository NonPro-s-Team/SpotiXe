import axios from "axios";
import { API_CONFIG, ENDPOINTS } from "../../config/api";
import { getToken } from "../../utils/tokenStorage";

/**
 * Axios instance for Song API
 * Base URL from config
 */
const songApi = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: API_CONFIG.TIMEOUT,
});

/**
 * Request interceptor - Add JWT Bearer token
 */
songApi.interceptors.request.use(
  (config) => {
    // Get JWT token from localStorage
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle errors globally
 */
songApi.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message =
      error.response?.data?.message || error.message || "An error occurred";
    console.error("API Error:", message);
    return Promise.reject(new Error(message));
  }
);

/**
 * Get all songs
 * @returns {Promise<Array>} Array of song objects
 */
export const getAllSongs = async () => {
  const response = await songApi.get(ENDPOINTS.SONGS);
  return response;
};

/**
 * Get song by ID
 * @param {number} id - Song ID
 * @returns {Promise<Object>} Song object
 */
export const getSongById = async (id) => {
  const response = await songApi.get(ENDPOINTS.SONG_BY_ID(id));
  return response;
};

/**
 * Create new song
 * @param {Object} songData - Song data
 * @param {AbortSignal} signal - Optional abort signal
 * @returns {Promise<Object>} Created song object
 */
export const createSong = async (songData, signal = null) => {
  const config = signal ? { signal } : {};
  const response = await songApi.post(ENDPOINTS.SONGS, songData, config);
  return response;
};

/**
 * Update song
 * @param {number} id - Song ID
 * @param {Object} songData - Updated song data
 * @returns {Promise<Object>} Updated song object
 */
export const updateSong = async (id, songData) => {
  const response = await songApi.put(ENDPOINTS.SONG_BY_ID(id), songData);
  return response;
};

/**
 * Delete song
 * @param {number} id - Song ID
 * @returns {Promise<Object>} Delete response
 */
export const deleteSong = async (id) => {
  const response = await songApi.delete(ENDPOINTS.SONG_BY_ID(id));
  return response;
};

/**
 * Upload song cover image
 * @param {number} id - Song ID
 * @param {File} file - Image file
 * @returns {Promise<Object>} Upload response with URL
 */
export const uploadCoverImage = async (id, file) => {
  const formData = new FormData();
  formData.append("cover", file);

  const response = await songApi.post(ENDPOINTS.SONG_COVER(id), formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response;
};

/**
 * Upload song audio file
 * @param {number} id - Song ID
 * @param {File} file - Audio file
 * @returns {Promise<Object>} Upload response with URL
 */
export const uploadAudioFile = async (id, file) => {
  const formData = new FormData();
  formData.append("audio", file);

  const response = await songApi.post(ENDPOINTS.SONG_AUDIO(id), formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
    onUploadProgress: (progressEvent) => {
      const percentCompleted = Math.round(
        (progressEvent.loaded * 100) / progressEvent.total
      );
      console.log(`Upload Progress: ${percentCompleted}%`);
    },
  });
  return response;
};

/**
 * Bulk import songs from CSV data
 * This function will:
 * 1. Parse CSV data (AudioFileUrl, CoverImageUrl)
 * 2. Extract metadata from audio files
 * 3. Find or create artists by name from metadata
 * 4. Find albums by title from metadata (set null if not found)
 * 5. Insert songs with proper IDs
 *
 * @param {Array} csvData - Array of parsed CSV rows
 * @param {AbortSignal} signal - Abort signal to cancel the import process
 * @returns {Promise<Object>} Import results { successful: [], failed: [] }
 */
export const bulkImportSongs = async (csvData, signal = null) => {
  const results = {
    successful: [],
    failed: [],
  };

  // Import required services and utilities
  const { getAllArtists, createArtist } = await import("./artistService");
  const { getAllAlbums } = await import("./albumService");
  const { extractAudioMetadata, formatReleaseDate } = await import(
    "../../utils/audioMetadata"
  );

  try {
    // Fetch all existing artists and albums
    const [existingArtists, existingAlbums] = await Promise.all([
      getAllArtists(),
      getAllAlbums(),
    ]);

    // Create maps for quick lookup
    const artistMap = new Map();
    existingArtists.forEach((artist) => {
      artistMap.set(artist.name.toLowerCase().trim(), artist.artistId);
    });

    const albumMap = new Map();
    existingAlbums.forEach((album) => {
      albumMap.set(album.title.toLowerCase().trim(), album.albumId);
    });

    // Process each song
    for (const row of csvData) {
      // Check if abort was requested
      if (signal?.aborted) {
        console.log('[Bulk Import] Process aborted by user');
        throw new Error('ABORTED');
      }

      const audioUrl = row.AudioFileUrl?.trim();
      const coverImageUrl = row.CoverImageUrl?.trim() || null;

      if (!audioUrl) {
        results.failed.push({
          title: "Unknown",
          artistName: "N/A",
          error: "URL file âm thanh không được để trống",
        });
        continue;
      }

      try {
        // Extract metadata from audio file
        console.log(`[Bulk Import] Extracting metadata from: ${audioUrl}`);
        const metadata = await extractAudioMetadata(audioUrl, signal);

        // Use metadata or fallback values
        const title = metadata.title || extractFilenameFromUrl(audioUrl);
        // Artist can be array or string - normalize to array
        const artistNames = Array.isArray(metadata.artist)
          ? metadata.artist
          : metadata.artist
          ? [metadata.artist]
          : ["Unknown Artist"];
        const albumTitle = metadata.album;
        const duration = metadata.duration || 0;
        const genre = metadata.genre || null;
        const releaseDate = formatReleaseDate(metadata.releaseDate);

        console.log(`[Bulk Import] Extracted metadata:`, {
          title,
          artistNames,
          albumTitle,
          duration,
          genre,
          releaseDate,
        });

        // Validate extracted data
        if (!title || duration === 0) {
          results.failed.push({
            title: title || audioUrl,
            artistName: artistNames.join(", "),
            error: "Không thể trích xuất metadata đầy đủ từ file audio",
          });
          continue;
        }

        // Find or create ALL artists
        const artistIds = [];
        const createdArtistNames = [];

        for (const artistName of artistNames) {
          const artistKey = artistName.toLowerCase().trim();

          console.log(
            `[Bulk Import] Looking for artist: "${artistName}" (key: "${artistKey}")`
          );

          if (artistMap.has(artistKey)) {
            const existingId = artistMap.get(artistKey);
            artistIds.push(existingId);
            console.log(
              `[Bulk Import] Found existing artist ID: ${existingId}`
            );
          } else {
            // Create new artist
            try {
              console.log(`[Bulk Import] Creating new artist: "${artistName}"`);
              const newArtist = await createArtist({
                name: artistName,
                bio: "",
                profileImageUrl: null,
                verified: false,
              }, signal);
              artistIds.push(newArtist.artistId);
              artistMap.set(artistKey, newArtist.artistId);
              createdArtistNames.push(artistName);
              console.log(
                `[Bulk Import] Created new artist: ${artistName} (ID: ${newArtist.artistId})`
              );
            } catch (error) {
              // If aborted during artist creation, throw to stop processing
              if (error.message === 'ABORTED' || error.name === 'AbortError' || error.code === 'ERR_CANCELED') {
                throw error;
              }
              
              console.error(
                `[Bulk Import] Failed to create artist ${artistName}:`,
                error
              );
              // Continue with other artists even if one fails
            }
          }
        }

        // If no artists were found/created, fail this song
        if (artistIds.length === 0) {
          results.failed.push({
            title: title,
            artistName: artistNames.join(", "),
            error: "Không thể tạo hoặc tìm thấy ca sĩ",
          });
          continue;
        }

        // Use first artist as primary artist for the song
        const primaryArtistId = artistIds[0];
        const primaryArtistName = artistNames[0];

        // Find album (set null if not found)
        let albumId = null;
        if (albumTitle && albumTitle.toLowerCase() !== "single") {
          const albumKey = albumTitle.toLowerCase();
          if (albumMap.has(albumKey)) {
            albumId = albumMap.get(albumKey);
          }
        }

        // Create song data
        const songData = {
          title: title,
          duration: duration,
          releaseDate: releaseDate,
          genre: genre,
          audioFileUrl: audioUrl,
          coverImageUrl: coverImageUrl,
          artistId: primaryArtistId,
          albumId: albumId,
          isActive: true,
        };

        // Check abort signal before creating song
        if (signal?.aborted) {
          console.log('[Bulk Import] Process aborted before creating song');
          throw new Error('ABORTED');
        }

        // Create song
        try {
          const createdSong = await createSong(songData, signal);

          // Prepare artist names for display
          const allArtistNames = artistNames.join(", ");
          const displayMessage =
            createdArtistNames.length > 0
              ? `${allArtistNames} (Tạo mới: ${createdArtistNames.join(", ")})`
              : allArtistNames;

          results.successful.push({
            title: songData.title,
            artistName: displayMessage,
            albumTitle: albumTitle || null,
            songId: createdSong.songId,
          });
          console.log(
            `[Bulk Import] Successfully created song: ${title} with artists: ${allArtistNames}`
          );
        } catch (error) {
          // If aborted during song creation, throw to stop processing
          if (error.message === 'ABORTED' || error.name === 'AbortError' || error.code === 'ERR_CANCELED') {
            throw error;
          }
          
          results.failed.push({
            title: songData.title,
            artistName: artistNames.join(", "),
            error: `Lỗi tạo bài hát: ${error.message}`,
          });
        }
      } catch (error) {
        // If aborted, throw immediately to stop the loop
        if (error.message === 'ABORTED' || error.name === 'AbortError' || error.code === 'ERR_CANCELED') {
          throw error;
        }
        
        results.failed.push({
          title: extractFilenameFromUrl(audioUrl),
          artistName: "N/A",
          error: `Lỗi xử lý file audio: ${error.message}`,
        });
      }
    }
  } catch (error) {
    // Re-throw abort errors to be handled by caller
    if (error.message === 'ABORTED' || error.name === 'AbortError' || error.code === 'ERR_CANCELED') {
      throw error;
    }
    
    console.error("Bulk import error:", error);
    throw new Error(`Lỗi khi nhập hàng loạt: ${error.message}`);
  }

  return results;
};

/**
 * Extract filename from URL as fallback title
 * @param {string} url - File URL
 * @returns {string} Filename without extension
 */
const extractFilenameFromUrl = (url) => {
  try {
    const urlObj = new URL(url);
    const pathname = urlObj.pathname;
    const filename = pathname.split("/").pop();
    return filename.replace(/\.[^/.]+$/, "").replace(/_/g, " ");
  } catch {
    return "Unknown";
  }
};

export default songApi;
